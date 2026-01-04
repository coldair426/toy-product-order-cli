package com.ckmall.order.bootstrap

import com.ckmall.order.adapter.inbound.cli.OrderCliAdapter
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
@Profile("cli")
class OrderCliApplication(
    private val orderCliAdapter: OrderCliAdapter,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        orderCliAdapter.start()
    }
}
