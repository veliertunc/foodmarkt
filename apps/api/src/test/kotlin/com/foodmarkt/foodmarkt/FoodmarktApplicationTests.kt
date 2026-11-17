package com.foodmarkt.foodmarkt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = [
		"spring.docker.compose.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:foodmarkt;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
	],
)
class FoodmarktApplicationTests {
	@LocalServerPort
	private var port: Int = 0

	private val client = HttpClient.newHttpClient()

	@Test
	fun catalogSupportsProductLifecycle() {
		val categories = request("GET", "/api/categories")
		assertEquals(200, categories.statusCode())
		listOf("bakery", "chocolate", "breakfast").forEach {
			assertTrue(categories.body().contains("\"$it\""))
		}

		val product = """{"name":"Sourdough","description":"Fresh loaf","price":90.50,"categorySlug":"bakery"}"""
		val created = request("POST", "/api/products", product)
		assertEquals(201, created.statusCode())
		val location = created.headers().firstValue("Location").orElseThrow()
		assertTrue(created.body().contains("\"TRY\""))
		assertEquals(200, request("GET", location).statusCode())

		val filtered = request("GET", "/api/products?category=bakery&page=0&size=1")
		assertEquals(200, filtered.statusCode())
		assertTrue(filtered.body().contains("\"Sourdough\""))
		assertEquals(400, request("GET", "/api/products?size=101").statusCode())
		assertEquals(400, request("POST", "/api/products", product.replace("90.50", "-1")).statusCode())
		assertEquals(404, request("POST", "/api/products", product.replace("bakery", "unknown")).statusCode())

		val updated = request("PUT", location, product.replace("bakery", "breakfast"))
		assertEquals(200, updated.statusCode())
		assertTrue(request("GET", location).body().contains("\"breakfast\""))
		assertEquals(204, request("DELETE", location).statusCode())
		assertEquals(404, request("GET", location).statusCode())
	}

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
