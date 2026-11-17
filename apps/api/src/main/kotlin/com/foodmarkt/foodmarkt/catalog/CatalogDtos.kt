package com.foodmarkt.foodmarkt.catalog

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ProductRequest(
    @field:NotBlank
    @field:Size(max = 120)
    val name: String,

    @field:Size(max = 2000)
    val description: String,

    @field:DecimalMin("0.01")
    @field:Digits(integer = 10, fraction = 2)
    val price: BigDecimal,

    @field:Pattern(regexp = "[a-z][a-z0-9-]{0,39}")
    val categorySlug: String,

    val available: Boolean = true,
)

data class CategoryResponse(val slug: String, val name: String)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val currency: String,
    val available: Boolean,
    val category: CategoryResponse,
)

data class ProductPage(
    val items: List<ProductResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
