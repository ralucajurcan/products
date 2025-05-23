package com.example.products.core.repository

import com.example.products.common.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()

    override suspend fun addProduct(product: Product) {
        products.add(product)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return flow { emit(products) }
    }

    override suspend fun getProductById(productId: Long): Product? {
        return products.find { it.id == productId }
    }

    override suspend fun syncProducts() {
        TODO("Not yet implemented")
    }

    override fun getProductAsFlow(id: Long): Flow<Product?> {
        TODO("Not yet implemented")
    }
}