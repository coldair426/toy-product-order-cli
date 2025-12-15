package com.ckmall.order.application.port.repository

import com.ckmall.order.domain.model.Inventory

interface InventoryRepository {
    fun findByProductId(productId: String): Inventory?

    fun findAll(): List<Inventory>

    fun save(inventory: Inventory)
}
