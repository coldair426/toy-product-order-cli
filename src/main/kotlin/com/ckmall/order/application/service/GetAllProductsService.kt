package com.ckmall.order.application.service

import com.ckmall.order.application.dto.ProductWithInventoryResponse
import com.ckmall.order.application.port.repository.InventoryRepository
import com.ckmall.order.application.port.repository.ProductRepository
import com.ckmall.order.application.usecase.GetAllProductsUseCase
import org.springframework.stereotype.Service

@Service
class GetAllProductsService(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
) : GetAllProductsUseCase {
    override fun execute(): List<ProductWithInventoryResponse> {
        val inventories = inventoryRepository.findAll().associateBy { it.productId }

        return productRepository
            .findAll()
            .map { product ->
                val inventory = inventories[product.id]

                ProductWithInventoryResponse(
                    id = product.id,
                    name = product.name,
                    price = product.price.amount,
                    inventory = inventory?.availableQuantity() ?: 0,
                )
            }
    }
}
