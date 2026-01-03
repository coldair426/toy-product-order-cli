package com.ckmall.order.adapter.inbound.cli

import com.ckmall.order.application.dto.CreateOrderResponse
import com.ckmall.order.application.dto.OrderLineRequest
import com.ckmall.order.application.usecase.CreateOrderUseCase
import com.ckmall.order.application.usecase.GetAllProductsUseCase
import com.ckmall.order.domain.exception.SoldOutException
import java.util.Scanner
import org.springframework.stereotype.Component

private const val SEPARATOR = "-----------------------------------"
private const val BANNERSEPARATOR = "==================================="

@Component
class OrderCliAdapter(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
) {
    private val scanner = Scanner(System.`in`)

    fun start() {
        printBanner()
        while (true) {
            when (startOptionSelector()) {
                "o" -> {
                    printAllProducts()
                    startOrderLoop()
                }

                "q" -> return
            }
        }
    }

    private fun printBanner() {
        println(BANNERSEPARATOR)
        println("       상품 주문 CLI 시스템")
        println(BANNERSEPARATOR)
    }

    private fun startOptionSelector(): String {
        while (true) {
            print("\n입력(o[order]: 주문, q[quit]: 종료) : ")
            val selectedOption = scanner.nextLine().trim()
            when (selectedOption) {
                "o" -> return "o"
                "q" -> return "q"
                else -> println("잘못된 입력입니다.")
            }
        }
    }

    private fun printAllProducts() {
        val products = getAllProductsUseCase.execute()

        println("상품번호        상품명                       판매가격        재고수 ")

        products.forEach {
            println(
                "${it.id}        ${it.name}        ${it.price}        ${it.inventory}",
            )
        }
    }

    private fun startOrderLoop() {
        val orderLines = mutableListOf<OrderLineRequest>()

        print("\n")
        try {
            while (true) {
                print("상품번호 : ")
                val productId = scanner.nextLine().trim()

                if (productId.isEmpty()) break

                print("수량 : ")
                val quantity = scanner.nextLine().trim().toInt()

                orderLines.add(
                    OrderLineRequest(
                        productId = productId,
                        quantity = quantity,
                    ),
                )
            }

            val response = createOrderUseCase.execute(orderLines)
            printResult(response)
        } catch (ex: SoldOutException) {
            println(ex.message)
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }

    private fun printResult(response: CreateOrderResponse) {
        println("주문 내역:")
        println(SEPARATOR)

        response.orderedItems.forEach {
            println("${it.productName} - ${it.quantity}개")
        }

        println(SEPARATOR)
        println("주문금액: ${formatMoney(response.itemsTotalPrice)}")

        if (response.shippingFee > 0L) {
            println("배송비: ${formatMoney(response.shippingFee)}")
        }

        println(SEPARATOR)
        println("지불금액: ${formatMoney(response.totalPrice)}")
        println(SEPARATOR)
    }

    private fun formatMoney(amount: Long): String = "%,d원".format(amount)
}
