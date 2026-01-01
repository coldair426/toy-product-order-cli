package com.ckmall.order.application.service

import com.ckmall.order.application.dto.CreateOrderResponse
import com.ckmall.order.application.dto.OrderLineRequest
import com.ckmall.order.application.dto.OrderedProductResponse
import com.ckmall.order.application.port.repository.InventoryRepository
import com.ckmall.order.application.port.repository.OrderRepository
import com.ckmall.order.application.port.repository.ProductRepository
import com.ckmall.order.application.usecase.CreateOrderUseCase
import com.ckmall.order.domain.model.Order
import com.ckmall.order.domain.model.vo.OrderItem
import com.ckmall.order.domain.policy.ShippingFeePolicy
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class CreateOrderService(
    private val orderRepository: OrderRepository,
    private val inventoryRepository: InventoryRepository,
    private val productRepository: ProductRepository,
    private val shippingFeePolicy: ShippingFeePolicy,
) : CreateOrderUseCase {
    override fun execute(requests: List<OrderLineRequest>): CreateOrderResponse {
        require(requests.isNotEmpty()) { "주문 항목은 최소 1개 이상" }

        val order = Order(id = UUID.randomUUID().toString())

        requests.forEach { request ->
            val product =
                productRepository.findById(request.productId)
                    ?: throw IllegalArgumentException("존재하지 않는 상품")
            val inventory =
                inventoryRepository.findByProductId(product.id)
                    ?: throw IllegalArgumentException("재고 정보가 없음")

            if (!inventory.isAvailable(request.quantity)) {
                throw IllegalArgumentException("재고 부족")
            }

            inventory.decrease(request.quantity)
            inventoryRepository.save(inventory)

            val orderItem =
                OrderItem(
                    productId = product.id,
                    productName = product.name,
                    quantity = request.quantity,
                    price = product.price,
                )

            order.addItem(orderItem)
        }

        order.applyShippingFee(shippingFeePolicy)
        orderRepository.save(order)

        return CreateOrderResponse(
            orderedItems =
                order.items().map {
                    OrderedProductResponse(
                        productName = it.productName,
                        quantity = it.quantity,
                    )
                },
            shippingFee = order.shippingFee().amount,
            itemsTotalPrice = order.itemsTotalPrice().amount,
            totalPrice = order.totalPrice().amount,
        )
    }
}
