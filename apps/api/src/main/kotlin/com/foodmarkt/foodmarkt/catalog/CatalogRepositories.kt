package com.foodmarkt.foodmarkt.catalog

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, String> {
    fun findAllByOrderBySlugAsc(): List<Category>
}

interface ProductRepository : JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = ["category"])
    override fun findAll(pageable: Pageable): Page<Product>

    @EntityGraph(attributePaths = ["category"])
    fun findByCategory_Slug(categorySlug: String, pageable: Pageable): Page<Product>
}
