package com.foodmarkt.foodmarkt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import tools.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FoodmarktApplicationTests {
	@LocalServerPort
	private var port: Int = 0

	private val client = HttpClient.newHttpClient()

	@Autowired
	private lateinit var json: ObjectMapper

	@Autowired
	private lateinit var jdbc: JdbcTemplate

	@Test
	fun catalogSupportsProductLifecycle() {
		val categories = request("GET", "/api/categories")
		assertEquals(200, categories.statusCode())
		listOf("bakery", "chocolate", "breakfast", "pantry", "drinks").forEach {
			assertTrue(categories.body().contains("\"$it\""))
		}

		val product = """{"name":"Sourdough","description":"Fresh loaf","price":90.50,"categorySlug":"bakery"}"""
		val created = request("POST", "/api/products", product)
		assertEquals(201, created.statusCode())
		val location = created.headers().firstValue("Location").orElseThrow()
		assertTrue(created.body().contains("\"TRY\""))
		assertEquals(200, request("GET", location).statusCode())

		val filtered = request("GET", "/api/products?category=bakery&page=0&size=100")
		assertEquals(200, filtered.statusCode())
		assertTrue(filtered.body().contains("\"Sourdough\""))
		assertTrue(filtered.body().contains("\"Sesame Simit\""))
		assertEquals(400, request("GET", "/api/products?size=101").statusCode())
		assertEquals(400, request("POST", "/api/products", product.replace("90.50", "-1")).statusCode())
		assertEquals(404, request("POST", "/api/products", product.replace("bakery", "unknown")).statusCode())

		val updated = request("PUT", location, product.replace("bakery", "breakfast"))
		assertEquals(200, updated.statusCode())
		assertTrue(request("GET", location).body().contains("\"breakfast\""))
		assertEquals(204, request("DELETE", location).statusCode())
		assertEquals(404, request("GET", location).statusCode())
	}

	@Test
	fun orderStoresCatalogPriceAndSurvivesProductDeletion() {
		val product = """{"name":"Brownie","description":"","price":25.75,"categorySlug":"chocolate"}"""
		val created = request("POST", "/api/products", product)
		assertEquals(201, created.statusCode())
		val productId = json.readTree(created.body()).get("id").asLong()
		val line = """{"productId":$productId,"quantity":2}"""
		val ordered = request("POST", "/api/orders", checkout(line))
		assertEquals(201, ordered.statusCode(), ordered.body())
		val receipt = json.readTree(ordered.body())
		assertEquals("TRY", receipt.get("currency").asText())
		assertEquals(51.5, receipt.get("total").asDouble())
		assertEquals("Brownie", receipt.get("items").get(0).get("productName").asText())
		assertTrue(!receipt.has("email"))

		assertEquals(204, request("DELETE", "/api/products/$productId").statusCode())
		val orderId = java.util.UUID.fromString(receipt.get("id").asText())
		val items = jdbc.queryForList("SELECT product_name, unit_price, quantity FROM order_items WHERE order_id = ?", orderId)
		assertEquals(1, items.size)
		assertEquals("Brownie", items.single()["PRODUCT_NAME"])
		assertEquals(java.math.BigDecimal("25.75"), items.single()["UNIT_PRICE"])
		assertEquals(2, items.single()["QUANTITY"])
	}

	@Test
	fun invalidAndUnavailableOrdersAreNotPersisted() {
		val before = jdbc.queryForObject("SELECT COUNT(*) FROM customer_orders", Long::class.java)
		val product = """{"name":"Roll","description":"","price":12,"categorySlug":"bakery","available":false}"""
		val created = request("POST", "/api/products", product)
		assertEquals(201, created.statusCode())
		val id = json.readTree(created.body()).get("id").asLong()
		val line = """{"productId":$id,"quantity":1}"""
		assertEquals(400, request("POST", "/api/orders", checkout("")).statusCode())
		assertEquals(400, request("POST", "/api/orders", checkout("null")).statusCode())
		assertEquals(400, request("POST", "/api/orders", checkout(line.replace(":1}", ":0}"))).statusCode())
		assertEquals(400, request("POST", "/api/orders", checkout(line.replace(":1}", ":1.5}"))).statusCode())
		assertEquals(400, request("POST", "/api/orders", checkout("$line,$line")).statusCode())
		assertEquals(409, request("POST", "/api/orders", checkout(line)).statusCode())
		assertEquals(409, request("POST", "/api/orders", checkout("""{"productId":999999,"quantity":1}""")).statusCode())
		assertEquals(before, jdbc.queryForObject("SELECT COUNT(*) FROM customer_orders", Long::class.java))
	}

	private fun checkout(lines: String) =
		"""{"customerName":"Test Customer","email":"customer@example.com","phone":"+905551112233","deliveryAddress":"Test address","items":[$lines]}"""

	private fun request(method: String, path: String, body: String? = null): HttpResponse<String> {
		val builder = HttpRequest.newBuilder(URI.create("http://localhost:$port$path"))
		val publisher = if (body == null) {
			HttpRequest.BodyPublishers.noBody()
		} else {
			builder.header("Content-Type", "application/json")
			HttpRequest.BodyPublishers.ofString(body)
		}
		return client.send(builder.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString())
	}
}
