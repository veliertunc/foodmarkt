package com.foodmarkt.foodmarkt.order

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository : JpaRepository<CustomerOrder, UUID>
