package com.foodmarkt.foodmarkt.order

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "customer_orders")
class CustomerOrder(
    @Column(nullable = false, length = 120)
    val customerName: String,
    @Column(nullable = false, length = 254)
    val email: String,
    @Column(nullable = false, length = 32)
    val phone: String,
    @Column(nullable = false, length = 500)
    val deliveryAddress: String,
    @Column(nullable = false, precision = 18, scale = 2)
    val total: BigDecimal,
    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
) {
    @OneToMany(mappedBy = "order", cascade = [CascadeType.PERSIST])
    val items: MutableList<OrderItem> = mutableListOf()
}

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    val order: CustomerOrder,
    @Column(nullable = false)
    val productId: Long,
    @Column(nullable = false, length = 120)
    val productName: String,
    @Column(nullable = false, precision = 12, scale = 2)
    val unitPrice: BigDecimal,
    @Column(nullable = false)
    val quantity: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
