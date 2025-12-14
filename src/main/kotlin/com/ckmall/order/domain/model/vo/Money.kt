package com.ckmall.order.domain.model.vo

class Money private constructor(
    val amount: Long,
) {
    init {
        require(amount >= 0) { "금액은 항상 0 이상" }
    }

    operator fun plus(other: Money): Money = Money(this.amount + other.amount)

    operator fun minus(other: Money): Money {
        require(this.amount >= other.amount) { "빼기 결과는 음수가 될 수 없음" }

        return Money(this.amount - other.amount)
    }

    operator fun times(multiplier: Int): Money {
        require(multiplier >= 0) { "곱하는 값은 음수가 될 수 없음" }

        return Money(this.amount * multiplier)
    }

    fun isZero(): Boolean = amount == 0L

    companion object {
        fun of(amount: Long): Money = Money(amount)

        val ZERO = Money(0)
    }
}
