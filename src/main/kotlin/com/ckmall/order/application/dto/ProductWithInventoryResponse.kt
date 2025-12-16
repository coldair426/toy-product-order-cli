package com.ckmall.order.application.dto

data class ProductWithInventoryResponse(
    val id: String,
    val name: String,
    val price: Long,
    val inventory: Int,
)
