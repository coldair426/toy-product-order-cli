package com.ckmall.order.domain.model

import com.ckmall.order.domain.model.vo.Money
import com.ckmall.order.domain.model.vo.OrderItem
import com.ckmall.order.domain.policy.ShippingFeePolicy
import kotlin.String

class Order(
    val id: String,
    private val items: MutableList<OrderItem> = mutableListOf(),
    private var shippingFee: Money = Money.ZERO,
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
                    productName = existingItem.productName,
                    quantity = existingItem.quantity + item.quantity,
                    price = existingItem.price,
                )
            }

        items.add(newItem)
    }

    fun applyShippingFee(policy: ShippingFeePolicy) {
        this.shippingFee = policy.calculate(itemsTotalPrice())
    }

    fun items(): List<OrderItem> = items.toList()

    fun shippingFee(): Money = this.shippingFee

    fun itemsTotalPrice(): Money = items.fold(Money.ZERO) { acc, item -> acc + item.totalPrice() }

    fun totalPrice(): Money = itemsTotalPrice() + shippingFee
}
