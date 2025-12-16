package com.ckmall.order.adapter.inbound.csv

import org.apache.commons.csv.CSVFormat
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class ProductCsvReader(
    @Value("classpath:products.csv")
    private val resource: Resource,
) {
    fun read(): List<ProductCsvRow> {
        resource.inputStream.bufferedReader().use { reader ->
            val format =
                CSVFormat.Builder
                    .create(CSVFormat.DEFAULT)
                    .setHeader() // 첫 줄을 header로 사용
                    .setSkipHeaderRecord(true) // header row는 record로 제외
                    .setTrim(true) // 공백 제거
                    .build()

            val records = format.parse(reader)

            return records.map { record ->
                ProductCsvRow(
                    id = record[0],
                    name = record[1],
                    price = parsePrice(record[2]),
                    inventory = record[3].toInt(),
                )
            }
        }
    }

    private fun parsePrice(raw: String): Long =
        raw
            .replace(",", "")
            .replace("원", "")
            .trim()
            .toLong()
}
