package com.ckmall.order.domain.model

import com.ckmall.order.domain.model.vo.Money
import com.ckmall.order.domain.model.vo.OrderLine
import com.ckmall.order.domain.policy.ShippingFeePolicy
import kotlin.String

class Order(
    val id: String,
    private val lines: MutableList<OrderLine> = mutableListOf(),
    private var shippingFee: Money = Money.ZERO,
) {
    fun addLine(line: OrderLine) {
        val existingLine = lines.find { it.productId == line.productId }

        val newLine =
            if (existingLine == null) {
                line
            } else {
                lines.remove(existingLine)
                OrderLine(
                    productId = existingLine.productId,
                    productName = existingLine.productName,
                    quantity = existingLine.quantity + line.quantity,
                    price = existingLine.price,
                )
            }

        lines.add(newLine)
    }

    fun applyShippingFee(policy: ShippingFeePolicy) {
        this.shippingFee = policy.calculate(linesTotalPrice())
    }

    fun lines(): List<OrderLine> = lines.toList()

    fun shippingFee(): Money = shippingFee

    fun linesTotalPrice(): Money = lines.fold(Money.ZERO) { acc, line -> acc + line.totalPrice() }

    fun totalPrice(): Money = linesTotalPrice() + shippingFee
}
