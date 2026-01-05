package com.ckmall.order.application.service

import com.ckmall.order.application.dto.CreateOrderResponse
import com.ckmall.order.application.dto.OrderLineRequest
import com.ckmall.order.application.dto.OrderedProductResponse
import com.ckmall.order.application.port.repository.InventoryRepository
import com.ckmall.order.application.port.repository.OrderRepository
import com.ckmall.order.application.port.repository.ProductRepository
import com.ckmall.order.application.usecase.CreateOrderUseCase
import com.ckmall.order.domain.exception.SoldOutException
import com.ckmall.order.domain.model.Order
import com.ckmall.order.domain.model.vo.OrderLine
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

        // in-memory repository는 트랜젝션를 직접 구현해야함
        requests.forEach { request ->
            val product =
                productRepository.findById(request.productId)
                    ?: throw IllegalArgumentException("존재하지 않는 상품")
            val inventory =
                inventoryRepository.findByProductId(product.id)
                    ?: throw IllegalArgumentException("재고 정보가 없음")

            if (!inventory.isAvailable(request.quantity)) {
                throw SoldOutException(request.productId)
            }
        }

        requests.forEach { request ->
            val product = productRepository.findById(request.productId)!!
            val inventory = inventoryRepository.findByProductId(product.id)!!

            inventory.decrease(request.quantity)
            inventoryRepository.save(inventory)

            val orderLine =
                OrderLine(
                    productId = product.id,
                    productName = product.name,
                    quantity = request.quantity,
                    price = product.price,
                )

            order.addLine(orderLine)
        }

        order.applyShippingFee(shippingFeePolicy)
        orderRepository.save(order)

        return CreateOrderResponse(
            orderedLines =
                order.lines().map {
                    OrderedProductResponse(
                        productName = it.productName,
                        quantity = it.quantity,
                    )
                },
            shippingFee = order.shippingFee().amount,
            linesTotalPrice = order.linesTotalPrice().amount,
            totalPrice = order.totalPrice().amount,
        )
    }
}
