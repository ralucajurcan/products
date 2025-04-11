package com.example.products.core.repository

import com.example.products.common.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductDataSource {
    fun getAllProducts(): Flow<List<Product>>

    suspend fun addProduct(product: Product)

    suspend fun getProductById(productId: Long): Product?
}