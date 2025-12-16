package com.ckmall.order.adapter.inbound.csv

data class ProductCsvRow(
    val id: String,
    val name: String,
    val price: Long,
    val inventory: Int,
)
