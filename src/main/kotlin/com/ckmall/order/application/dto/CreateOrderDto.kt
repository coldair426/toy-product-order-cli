package com.ckmall.order.application.dto

data class OrderLineRequest(
    val productId: String,
    val quantity: Int,
)

data class CreateOrderResponse(
    val orderedItems: List<OrderedProductResponse>,
    val totalPrice: Long,
)

data class OrderedProductResponse(
    val productName: String,
    val quantity: Int,
)
