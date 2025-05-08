package com.example.products.core.repository

import com.example.products.common.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun addProduct(product: Product)
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(productId: Long): Product?
    suspend fun syncProducts()
}