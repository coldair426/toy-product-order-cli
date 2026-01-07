package com.ckmall.order.domain.policy

import com.ckmall.order.domain.model.vo.Money
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals

class DefaultShippingFeePolicyTest {
    @Test
    fun `상품 금액 합이 무료 배송 기준 미만이면 배송비가 발생한다`() {
        // given
        val policy = DefaultShippingFeePolicy()
        val linesTotalPrice = Money.of(49_999)

        // when
        val shippingFee = policy.calculate(linesTotalPrice)

        // then
        assertEquals(Money.of(2_500), shippingFee)
    }

    @Test
    fun `상품 금액 합이 무료 배송 기준 이상이면 배송비는 0이다`() {
        // given
        val policy = DefaultShippingFeePolicy()
        val linesTotalPrice = Money.of(50_000)

        // when
        val shippingFee = policy.calculate(linesTotalPrice)

        // then
        assertEquals(Money.ZERO, shippingFee)
    }
}
