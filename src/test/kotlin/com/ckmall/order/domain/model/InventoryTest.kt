package com.ckmall.order.domain.model

import com.ckmall.order.domain.exception.SoldOutException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InventoryTest {
    @Test
    fun `재고 수량이 음수이면 Inventory 생성 시 예외가 발생한다`() {
        // given
        val quantity = -1

        // when & then
        assertThrows<IllegalArgumentException> {
            Inventory(
                productId = "테스트 상품ID",
                quantity = quantity,
            )
        }
    }

    @Test
    fun `재고보다 많은 수량을 차감하면 SoldOutException이 발생한다`() {
        // given
        val inventory =
            Inventory(
                productId = "테스트 상품ID",
                quantity = 10,
            )
        val orderedQuantity = 11

        // when & then
        assertThrows<SoldOutException> {
            inventory.decrease(orderedQuantity)
        }
    }
}
