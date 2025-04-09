package com.example.products.framework.remote

data class FakeProductResponse(val products: List<FakeProduct>)

data class FakeProduct(
    val id: Int,
    val title: String,
    val description: String,
    val thumbnail: String
)
