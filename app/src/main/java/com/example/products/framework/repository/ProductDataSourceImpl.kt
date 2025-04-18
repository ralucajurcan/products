package com.example.products.framework.repository

import com.example.products.common.model.Product
import com.example.products.core.repository.ProductDataSource
import com.example.products.framework.db.ProductDao
import com.example.products.framework.mapper.toEntity
import com.example.products.framework.mapper.toProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductDataSourceImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductDataSource {

    override suspend fun addProduct(product: Product) {
        productDao.addProduct(product.toEntity())
    }

    override suspend fun getProductById(productId: Long): Product? {
        return productDao.getProductById(productId)?.toProduct()
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { list ->
            list.map { it.toProduct() }
        }
    }
}