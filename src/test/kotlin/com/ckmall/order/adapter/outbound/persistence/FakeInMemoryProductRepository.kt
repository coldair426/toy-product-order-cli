package com.ckmall.order.adapter.outbound.persistence

import com.ckmall.order.application.port.repository.ProductRepository
import com.ckmall.order.domain.model.Product
import kotlin.String

class FakeInMemoryProductRepository(
    products: List<Product>,
) : ProductRepository {
    private val products = products.associateBy { it.id }.toMutableMap()

    override fun findById(id: String): Product? = products[id]

    override fun findAll(): List<Product> = products.values.toList()

    override fun save(product: Product) {
        products[product.id] = product
    }
}
