package com.ckmall.order.application.service

import com.ckmall.order.adapter.outbound.persistence.FakeInMemoryInventoryRepository
import com.ckmall.order.adapter.outbound.persistence.FakeInMemoryOrderRepository
import com.ckmall.order.adapter.outbound.persistence.FakeInMemoryProductRepository
import com.ckmall.order.application.dto.OrderLineRequest
import com.ckmall.order.domain.exception.SoldOutException
import com.ckmall.order.domain.model.Inventory
import com.ckmall.order.domain.model.Product
import com.ckmall.order.domain.model.vo.Money
import com.ckmall.order.domain.policy.DefaultShippingFeePolicy
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CreateOrderServiceTest {
    @Test
    fun `여러 주문이 동시에 재고를 초과하면 SoldOutException이 발생한다`() {
        // given
        val initialQuantity = 30_000
        val orderQuantity = 10
        val threadCount = 3_100
        val expectedSoldOutExceptionCount = 100

        val createOrderService =
            CreateOrderService(
                orderRepository = FakeInMemoryOrderRepository(),
                inventoryRepository =
                    FakeInMemoryInventoryRepository(
                        inventories =
                            listOf(
                                Inventory(
                                    productId = "테스트 상품ID",
                                    quantity = initialQuantity,
                                ),
                            ),
                    ),
                productRepository =
                    FakeInMemoryProductRepository(
                        products =
                            listOf(
                                Product(
                                    id = "테스트 상품ID",
                                    name = "테스트 상품명",
                                    price = Money.of(3_000),
                                ),
                            ),
                    ),
                shippingFeePolicy = DefaultShippingFeePolicy(),
            )

        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val capturedExceptions = ConcurrentLinkedQueue<Exception>()

        // when
        repeat(threadCount) {
            executor.submit {
                try {
                    createOrderService.execute(
                        listOf(
                            OrderLineRequest(
                                productId = "테스트 상품ID",
                                quantity = orderQuantity,
                            ),
                        ),
                    )
                } catch (e: Exception) {
                    capturedExceptions.add(e)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // then
        val soldOutCount = capturedExceptions.filterIsInstance<SoldOutException>().size
        assertEquals(expectedSoldOutExceptionCount, soldOutCount)
    }
}
