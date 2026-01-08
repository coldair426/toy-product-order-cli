package com.ckmall.order.adapter.outbound.persistence

import com.ckmall.order.application.port.repository.InventoryRepository
import com.ckmall.order.domain.model.Inventory

class FakeInMemoryInventoryRepository(
    inventories: List<Inventory>,
) : InventoryRepository {
    private val inventories = inventories.associateBy { it.productId }.toMutableMap()

    override fun findByProductId(productId: String): Inventory? = inventories[productId]

    override fun findAll(): List<Inventory> = inventories.values.toList()

    override fun save(inventory: Inventory) {
        inventories[inventory.productId] = inventory
    }
}
