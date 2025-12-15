package com.ckmall.order.application.port.repository

import com.ckmall.order.domain.model.Product

interface ProductRepository {
    fun findById(id: String): Product?

    fun findAll(): List<Product>
}
