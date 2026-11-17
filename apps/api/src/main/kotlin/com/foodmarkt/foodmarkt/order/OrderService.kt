package com.foodmarkt.foodmarkt.order

import com.foodmarkt.foodmarkt.catalog.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class OrderService(
    private val orders: OrderRepository,
    private val products: ProductRepository,
) {
    @Transactional
    fun place(request: PlaceOrderRequest): OrderReceipt {
        if (request.phone.count { it in '0'..'9' } !in 7..15) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid phone number")
        }
        val items = request.items.map {
            it ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Order items cannot be null")
        }
        val productIds = items.map { it.productId }.toSet()
        if (productIds.size != items.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Each product must appear once")
        }
        val catalog = products.findAllById(productIds).associateBy { it.id }
        val lines = items.map { item ->
            val product = catalog[item.productId]?.takeIf { it.available }
                ?: throw ResponseStatusException(HttpStatus.CONFLICT, "Product ${item.productId} is unavailable")
            OrderReceiptItem(item.productId, product.name, product.price, item.quantity)
        }
        val order = CustomerOrder(
            customerName = request.customerName.trim(),
            email = request.email.trim(),
            phone = request.phone.trim(),
            deliveryAddress = request.deliveryAddress.trim(),
            total = lines.sumOf { it.unitPrice * it.quantity.toBigDecimal() },
        )
        lines.forEach {
            order.items.add(OrderItem(order, it.productId, it.productName, it.unitPrice, it.quantity))
        }
        val saved = orders.save(order)
        return OrderReceipt(requireNotNull(saved.id), saved.createdAt, saved.total, "TRY", lines)
    }
}
