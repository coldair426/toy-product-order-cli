package com.ckmall.order.application.dto

data class OrderLineRequest(
    val productId: String,
    val quantity: Int,
)

data class CreateOrderResponse(
    val orderedLines: List<OrderedProductResponse>,
    val linesTotalPrice: Long,
    val shippingFee: Long,
    val totalPrice: Long,
)

data class OrderedProductResponse(
    val productName: String,
    val quantity: Int,
)
