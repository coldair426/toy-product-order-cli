package com.ckmall.order.domain.model

import com.ckmall.order.domain.model.vo.Money

class Product(
    val id: String,
    val name: String,
    val price: Money,
)
