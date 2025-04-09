package com.example.products.common.model

data class Product(
    var title: String,
    var description: String,
    var creationTime: Long,
    var updateTime: Long,
    var id: Long = 0,
    val imageUrl: String?
)
