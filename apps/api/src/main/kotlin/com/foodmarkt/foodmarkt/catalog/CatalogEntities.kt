package com.foodmarkt.foodmarkt.catalog

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "categories")
class Category(
    @Id
    @Column(length = 40)
    val slug: String,

    @Column(nullable = false, length = 80)
    val name: String,
)

@Entity
@Table(name = "products")
class Product(
    @Column(nullable = false, length = 120)
    var name: String,

    @Column(nullable = false, length = 2000)
    var description: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var price: BigDecimal,

    @Column(nullable = false)
    var available: Boolean,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_slug", nullable = false)
    var category: Category,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
