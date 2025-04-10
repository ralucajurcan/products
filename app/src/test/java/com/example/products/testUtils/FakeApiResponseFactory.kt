package com.example.products.testUtils

import com.example.products.framework.remote.FakeProduct
import com.example.products.framework.remote.FakeProductResponse

object FakeApiResponseFactory {

    fun createFakeProductResponse(count: Int): FakeProductResponse {
        val products = (1..count).map {
            FakeProduct(
                id = it,
                title = "Fake Product $it",
                description = "Description for product $it",
                thumbnail = "https://example.com/product$it.png"
            )
        }
        return FakeProductResponse(products = products)
    }

}