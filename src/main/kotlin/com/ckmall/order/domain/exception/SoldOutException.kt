package com.ckmall.order.domain.exception

class SoldOutException(
    productId: String,
) : RuntimeException(
        "상품[$productId] 재고 부족",
    )
