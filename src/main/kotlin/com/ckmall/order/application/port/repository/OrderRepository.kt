package com.ckmall.order.application.port.repository

import com.ckmall.order.domain.model.Order

interface OrderRepository {
    fun save(order: Order): Order
}
