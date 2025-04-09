package com.example.products.framework.repository

import com.example.products.common.model.Product
import com.example.products.core.repository.ProductDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProductRepositoryImplTest {

    private lateinit var productDataSource: ProductDataSource
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        productDataSource = mock()
        repository = ProductRepositoryImpl(productDataSource)
    }

    @Test
    fun addProduct_shouldCallProductDataSource() = runTest {
        // given
        val product = Product("Title", "Content", 1L, 1L, 0L, "")

        // when
        repository.addProduct(product)

        // then
        verify(productDataSource).addProduct(product)
    }

    @Test
    fun getAllProducts_shouldReturnProductsFromDataSource() = runTest {
        // given
        val products = listOf(Product("Title", "Content", 1L, 1L, 0L, ""))
        whenever(productDataSource.getAllProducts()).thenReturn(flowOf(products))

        // when
        val result = repository.getAllProducts()

        // then
        result.collect{
            assert(it == products)
        }
    }

    @Test
    fun getProductById_shouldReturnProductFromDataSource() = runTest {
        // given
        val product = Product("Title", "Content", 1L, 1L, 0L, "")
        whenever(productDataSource.getProductById(any())).thenReturn(product)

        // when
        val result = repository.getProductById(product.id)

        // then
        assertEquals(product, result)
    }
}