package com.foodmarkt.foodmarkt.catalog

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional(readOnly = true)
class CatalogService(
    private val categories: CategoryRepository,
    private val products: ProductRepository,
) {
    fun listCategories(): List<CategoryResponse> =
        categories.findAllByOrderBySlugAsc().map { it.toResponse() }

    fun getCategory(slug: String): CategoryResponse = requireCategory(slug).toResponse()

    fun listProducts(categorySlug: String?, page: Int, size: Int): ProductPage {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        if (pageable.offset > Int.MAX_VALUE) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Page offset is too large")
        }
        val result = if (categorySlug == null) {
            products.findAll(pageable)
        } else {
            requireCategory(categorySlug)
            products.findByCategory_Slug(categorySlug, pageable)
        }
        return ProductPage(
            result.content.map { it.toResponse() },
            result.number,
            result.size,
            result.totalElements,
            result.totalPages,
        )
    }

    fun getProduct(id: Long): ProductResponse = requireProduct(id).toResponse()

    @Transactional
    fun createProduct(request: ProductRequest): ProductResponse {
        val product = Product(
            name = request.name.trim(),
            description = request.description.trim(),
            price = request.price.setScale(2),
            available = request.available,
            category = requireCategory(request.categorySlug),
        )
        return products.save(product).toResponse()
    }

    @Transactional
    fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        val product = requireProduct(id)
        val category = requireCategory(request.categorySlug)
        product.name = request.name.trim()
        product.description = request.description.trim()
        product.price = request.price.setScale(2)
        product.available = request.available
        product.category = category
        return product.toResponse()
    }

    @Transactional
    fun deleteProduct(id: Long) {
        products.delete(requireProduct(id))
    }

    private fun requireCategory(slug: String): Category =
        categories.findById(slug).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Category '$slug' was not found")
        }

    private fun requireProduct(id: Long): Product =
        products.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Product $id was not found")
        }

    private fun Category.toResponse() = CategoryResponse(slug, name)

    private fun Product.toResponse() = ProductResponse(
        id = requireNotNull(id),
        name = name,
        description = description,
        price = price,
        currency = "TRY",
        available = available,
        category = category.toResponse(),
    )
}
