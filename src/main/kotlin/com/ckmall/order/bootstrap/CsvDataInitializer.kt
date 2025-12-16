package com.ckmall.order.bootstrap

import com.ckmall.order.adapter.inbound.csv.ProductCsvReader
import com.ckmall.order.application.port.repository.InventoryRepository
import com.ckmall.order.application.port.repository.ProductRepository
import com.ckmall.order.domain.model.Inventory
import com.ckmall.order.domain.model.Product
import com.ckmall.order.domain.model.vo.Money
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class CsvDataInitializer(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
    private val productCsvReader: ProductCsvReader,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val rows = productCsvReader.read()

        rows.forEach { row ->
            val product =
                Product(
                    id = row.id,
                    name = row.name,
                    price = Money.of(row.price),
                )

            val inventory =
                Inventory(
                    productId = row.id,
                    quantity = row.inventory,
                )

            productRepository.save(product)
            inventoryRepository.save(inventory)
        }
    }
}
