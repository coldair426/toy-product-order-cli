package com.ckmall.order.application.usecase

import com.ckmall.order.application.dto.ProductWithInventoryResponse

interface GetAllProductsUseCase {
    fun execute(): List<ProductWithInventoryResponse>
}
