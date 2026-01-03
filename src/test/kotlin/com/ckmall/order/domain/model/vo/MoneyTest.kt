package com.ckmall.order.domain.model.vo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MoneyTest {
    @Test
    fun `음수로 Money를 생성하면 예외가 발생한다`() {
        // given
        val amount = -1L

        // when & then
        assertThrows<IllegalArgumentException> {
            Money.of(amount)
        }
    }

    @Test
    fun `뺄셈 결과가 음수가 되면 예외가 발생한다`() {
        // given
        val baseMoney = Money.of(1)
        val subtractMoney = Money.of(2)

        // when & then
        assertThrows<IllegalArgumentException> {
            baseMoney - subtractMoney
        }
    }

    @Test
    fun `Money에 음수를 곱하면 예외가 발생한다`(){
        // given
        val baseMoney = Money.of(10)
        val negativeMultiplier = -1

        assertThrows<IllegalArgumentException>{
            baseMoney * negativeMultiplier
        }
    }
}
