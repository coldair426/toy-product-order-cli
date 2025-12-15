package com.ckmall.order.adapter.outbound.persistence

import com.ckmall.order.application.port.repository.ProductRepository
import com.ckmall.order.domain.model.Product

class InMemoryProductRepository : ProductRepository {
    private val storedProducts = mutableMapOf<String, Product>()

    override fun findById(id: String): Product? = storedProducts[id]

    override fun findAll(): List<Product> = storedProducts.values.toList()
}
