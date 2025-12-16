package com.ckmall.order.adapter.outbound.persistence

import com.ckmall.order.application.port.repository.OrderRepository
import com.ckmall.order.domain.model.Order
import org.springframework.stereotype.Repository

@Repository
class InMemoryOrderRepository : OrderRepository {
    private val storedOrders = mutableMapOf<String, Order>()

    override fun save(order: Order): Order {
        storedOrders[order.id] = order
        return order
    }
}
