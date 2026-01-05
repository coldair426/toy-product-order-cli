package com.ckmall.order.domain.policy

import com.ckmall.order.domain.model.vo.Money

interface ShippingFeePolicy {
    fun calculate(linesTotalPrice: Money): Money
}
