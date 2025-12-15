package com.ckmall.order.adapter.outbound.persistence

import com.ckmall.order.application.port.repository.InventoryRepository
import com.ckmall.order.domain.model.Inventory
import kotlin.collections.set

class InMemoryInventoryRepository : InventoryRepository {
    private val storedInventories = mutableMapOf<String, Inventory>()

    override fun findByProductId(productId: String): Inventory? = storedInventories[productId]

    override fun findAll(): List<Inventory> = storedInventories.values.toList()

    override fun save(inventory: Inventory) {
        storedInventories[inventory.productId] = inventory
    }
}
