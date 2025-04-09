package com.example.products.core.usecase

import com.example.products.common.model.Product
import com.example.products.core.repository.ProductRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddProductTest {

    private val productRepository = mockk<ProductRepository>(relaxed = true) // relaxed = true => ignore undefined behavior
    private val addProduct = AddProduct(productRepository)

    @Test
    fun addProduct_success() = runTest {
        // given
        val product = Product(
            title = "test product",
            description = "test description",
            creationTime = 1234L,
            updateTime = 1234L,
            imageUrl = "https://example.com/img.jpg"
        )

        // when
        addProduct(product)

        // then
        // assert that a suspend fun was called
        coVerify { productRepository.addProduct(product) }
    }
}