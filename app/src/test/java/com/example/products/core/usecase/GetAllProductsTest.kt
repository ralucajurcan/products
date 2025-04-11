package com.example.products.core.usecase

import com.example.products.common.model.Product
import com.example.products.core.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllProductsTest {

    private val productRepository = mockk<ProductRepository>(relaxed = true)
    private lateinit var getAllProducts: GetAllProducts

    private val testProducts = listOf(
        Product("Title1", "Content1", 1L, 1L, 1L, "https://example.com/1"),
        Product("Title2", "Content2", 2L, 2L, 2L, "https://example.com/2")
    )

    @Before
    fun setup() {
        // return a fake flow
        coEvery { productRepository.getAllProducts() } returns flowOf(testProducts)
        getAllProducts = GetAllProducts(productRepository)
    }

    @Test
    fun getAllProducts_success() = runTest {
        // given/when
        val result = getAllProducts()

        // then
        result.collect {
            products -> assertEquals(testProducts, products)
        }
    }

}