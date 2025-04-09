package com.example.products.framework.repository

import com.example.products.common.model.Product
import com.example.products.core.repository.ProductDataSource
import com.example.products.core.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDataSource: ProductDataSource
) : ProductRepository {

    override suspend fun addProduct(product: Product) {
        productDataSource.addProduct(product)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDataSource.getAllProducts()
    }

    override suspend fun getProductById(productId: Long): Product? {
        return productDataSource.getProductById(productId)
    }
}