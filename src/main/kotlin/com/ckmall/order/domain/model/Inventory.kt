package com.ckmall.order.domain.model

class Inventory(
    val productId: String,
    private var quantity: Int,
) {
    init {
        require(quantity >= 0) { "재고는 항상 0 이상" }
    }

    fun decrease(amount: Int) {
        require(amount > 0) { "차감 수량은 0 이상" }
        require(quantity >= amount) { "재고 부족" }

        quantity -= amount
    }

    fun isAvailable(amount: Int): Boolean = quantity >= amount
}
