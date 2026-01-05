package com.ckmall.order.domain.policy

import com.ckmall.order.domain.model.vo.Money
import org.springframework.stereotype.Component

@Component
class DefaultShippingFeePolicy : ShippingFeePolicy {
    companion object {
        private val FREE_SHIPPING_THRESHOLD = Money.of(50_000)
        private val DEFAULT_SHIPPING_FEE = Money.of(2_500)
    }

    override fun calculate(linesTotalPrice: Money): Money =
        if (linesTotalPrice < FREE_SHIPPING_THRESHOLD) DEFAULT_SHIPPING_FEE else Money.ZERO
}
