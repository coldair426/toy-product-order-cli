package com.ckmall.order.domain.model

import com.ckmall.order.domain.model.vo.Money
import com.ckmall.order.domain.model.vo.OrderLine
import com.ckmall.order.domain.policy.ShippingFeePolicy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OrderTest {
    @Test
    fun `Order에 동일한 상품이 추가되면 OrderLine의 수량이 누적된다(병합된다)`() {
        // given
        val order =
            Order(
                id = "테스트 주문ID",
                lines =
                    mutableListOf(
                        OrderLine(
                            productId = "테스트 상품ID",
                            productName = "테스트 상품명",
                            quantity = 10,
                            price = Money.of(10_000),
                        ),
                    ),
                shippingFee = Money.of(2_500),
            )
        val orderLine =
            OrderLine(
                productId = "테스트 상품ID",
                productName = "테스트 상품명",
                quantity = 7,
                price = Money.of(10_000),
            )

        // when
        order.addLine(orderLine)

        // then
        assertEquals(17, order.lines().first().quantity)
    }

    @Test
    fun `Order는 배송비 정책을 통해 계산된 배송비를 반영한다`() {
        // given
        val order =
            Order(
                id = "테스트 주문ID",
                lines =
                    mutableListOf(
                        OrderLine(
                            productId = "테스트 상품ID",
                            productName = "테스트 상품명",
                            quantity = 10,
                            price = Money.of(10_000),
                        ),
                    ),
            )
        val shippingFeePolicy =
            object : ShippingFeePolicy {
                override fun calculate(linesTotalPrice: Money): Money = Money.of((linesTotalPrice * 2).amount)
            }

        // when
        order.applyShippingFee(shippingFeePolicy)
        // then
        assertEquals(Money.of(200_000), order.shippingFee())
    }

    @Test
    fun `Order의 총 금액은 상품 금액 합계와 배송비의 합이다`() {
        // given
        val order =
            Order(
                id = "테스트 주문ID",
                lines =
                    mutableListOf(
                        OrderLine(
                            productId = "테스트 상품ID",
                            productName = "테스트 상품명",
                            quantity = 1,
                            price = Money.of(10_000),
                        ),
                    ),
                shippingFee = Money.of(2_500),
            )

        // when & then
        assertEquals(12_500L, order.totalPrice().amount)
    }
}
