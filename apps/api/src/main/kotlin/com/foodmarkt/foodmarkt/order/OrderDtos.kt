package com.foodmarkt.foodmarkt.order

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PlaceOrderRequest(
    @field:NotBlank @field:Size(max = 120)
    val customerName: String,
    @field:NotBlank @field:Email @field:Size(max = 254)
    val email: String,
    @field:Pattern(regexp = "[+0-9() .-]{7,32}")
    val phone: String,
    @field:NotBlank @field:Size(max = 500)
    val deliveryAddress: String,
    @field:Size(min = 1, max = 50) @field:Valid
    val items: List<OrderItemRequest?>,
)

data class OrderItemRequest(
    @field:Positive val productId: Long,
    @field:Min(1) @field:Max(99) val quantity: Int,
)

data class OrderReceipt(
    val id: UUID,
    val createdAt: Instant,
    val total: BigDecimal,
    val currency: String,
    val items: List<OrderReceiptItem>,
)

data class OrderReceiptItem(
    val productId: Long,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
)
