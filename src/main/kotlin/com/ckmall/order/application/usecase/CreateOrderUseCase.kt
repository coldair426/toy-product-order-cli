package com.ckmall.order.application.usecase

import com.ckmall.order.application.dto.CreateOrderResponse
import com.ckmall.order.application.dto.OrderLineRequest

interface CreateOrderUseCase {
    fun execute(requests: List<OrderLineRequest>): CreateOrderResponse
}
