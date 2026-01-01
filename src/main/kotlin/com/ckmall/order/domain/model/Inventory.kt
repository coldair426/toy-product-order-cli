package com.ckmall.order.domain.model

import com.ckmall.order.domain.exception.SoldOutException

class Inventory(
    val productId: String,
    private var quantity: Int,
) {
    init {
        require(quantity >= 0) { "재고는 항상 0 이상" }
    }

    fun availableQuantity(): Int = quantity

    fun decrease(amount: Int) {
        require(amount > 0) { "차감 수량은 0 이상" }
        if (quantity < amount) throw SoldOutException(productId)

        quantity -= amount
    }

    fun isAvailable(amount: Int): Boolean = quantity >= amount
}
