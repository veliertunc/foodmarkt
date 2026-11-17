package com.foodmarkt.foodmarkt.catalog

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/products")
class ProductController(private val catalog: CatalogService) {
    @GetMapping
    fun list(
        @RequestParam(required = false) category: String?,
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) size: Int,
    ): ProductPage = catalog.listProducts(category, page, size)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ProductResponse = catalog.getProduct(id)

    @PostMapping
    fun create(@Valid @RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val product = catalog.createProduct(request)
        return ResponseEntity.created(URI.create("/api/products/${product.id}")).body(product)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: ProductRequest): ProductResponse =
        catalog.updateProduct(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        catalog.deleteProduct(id)
    }
}
