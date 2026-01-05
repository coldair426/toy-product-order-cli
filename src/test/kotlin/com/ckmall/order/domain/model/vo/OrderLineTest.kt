package com.ckmall.order.domain.model.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderLineTest {
    @Test
    fun `주문 수량이 0이하면 OrderLine 생성 시 예외가 발생한다`() {
        // given
        val quantity = 0

        // when & then
        assertThrows<IllegalArgumentException> {
            OrderLine(
                productId = "테스트 상품ID",
                productName = "테스트 상품명",
                quantity = quantity,
                price = Money.of(200000),
            )
        }
    }

    @Test
    fun `주문 총 금액은 price(단가)와 quantity(수량)의 곱이다`() {
        // given
        val quantity = 10
        val price = Money.of(3000)

        val orderLine =
            OrderLine(
                productId = "테스트 상품ID",
                productName = "테스트 상품명",
                quantity = quantity,
                price = price,
            )

        // when
        val totalPrice = orderLine.totalPrice()

        // then
        assertEquals(Money.of(30000), totalPrice)
    }
}
