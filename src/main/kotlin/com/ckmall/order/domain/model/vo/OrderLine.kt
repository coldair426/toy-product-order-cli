package com.ckmall.order.domain.model.vo

data class OrderLine(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Money,
) {
    init {
        require(quantity > 0) { "수량은 1 이상" }
    }

    fun totalPrice(): Money = price * quantity
}
