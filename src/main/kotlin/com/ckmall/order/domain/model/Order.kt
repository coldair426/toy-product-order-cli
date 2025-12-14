package com.ckmall.order.domain.model

import com.ckmall.order.domain.model.vo.Money
import com.ckmall.order.domain.model.vo.OrderItem
import kotlin.String

class Order(
    val id: String,
    private val items: MutableList<OrderItem> = mutableListOf(),
) {
    fun addItem(item: OrderItem) {
        val existingItem = items.find { it.productId == item.productId }

        val newItem =
            if (existingItem == null) {
                item
            } else {
                items.remove(existingItem)
                OrderItem(
                    productId = existingItem.productId,
                    quantity = existingItem.quantity + item.quantity,
                    price = existingItem.price,
                )
            }

        items.add(newItem)
    }

    fun totalPrice(): Money = items.fold(Money.ZERO) { acc, item -> acc + item.totalPrice() }
}
