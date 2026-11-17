package com.foodmarkt.foodmarkt.catalog

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val catalog: CatalogService) {
    @GetMapping
    fun list(): List<CategoryResponse> = catalog.listCategories()

    @GetMapping("/{slug}")
    fun get(@PathVariable slug: String): CategoryResponse = catalog.getCategory(slug)
}
