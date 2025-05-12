package com.example.products.core.usecase

import com.example.products.common.model.Product
import com.example.products.core.repository.FakeProductRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetProductByIdTest {

    private lateinit var useCases: UseCases
    private lateinit var fakeProductRepository: FakeProductRepository

    @Before
    fun setup() {
        fakeProductRepository = FakeProductRepository()

        useCases = UseCases(
            addProduct = AddProduct(fakeProductRepository),
            getAllProducts = GetAllProducts(fakeProductRepository),
            getProduct = { id -> fakeProductRepository.getProductById(id) },
            syncProducts = { fakeProductRepository.syncProducts() }
        )
    }

    @Test
    fun getProductById_success() = runTest {
        // given
        val product = Product(
            title = "test product",
            description = "test content",
            creationTime = 1234L,
            updateTime = 1234L,
            imageUrl = "https://example.com/img.jpg",
            id = 1L
        )
        useCases.addProduct(product)

        // when
        val result = useCases.getProduct(product.id)

        // then
        assertEquals(product, result)
    }
}