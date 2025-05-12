package com.example.products.presentation

import com.example.products.common.model.Product
import com.example.products.core.usecase.UseCases
import com.example.products.presentation.product.ProductViewModel
import com.example.products.testUtils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class ProductViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val useCases: UseCases = mockk(relaxed = true)
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        viewModel = ProductViewModel(useCases)
    }

    @Test
    fun saveProductValidData() = runTest {
        // given
        val product = Product(
            title = "Test Product",
            description = "Valid Description",
            imageUrl = "https://example.com/image.jpg",
            creationTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis()
        )

        // when
        viewModel.saveProduct(product)
        advanceUntilIdle()

        // then
        coVerify { useCases.getAllProducts() }
        assertNull(viewModel.validationError.value)
        assertTrue(viewModel.saved.value)
    }


    @Test
    fun saveProductWithInvalidData() = runTest {
        // given
        val product = Product(
            title = "",
            description = "Valid Description",
            imageUrl = "https://example.com/image.jpg",
            creationTime = 0,
            updateTime = 0
        )

        // when
        viewModel.saveProduct(product)
        advanceUntilIdle()

        // then
        assertEquals("Title cannot be blank", viewModel.validationError.value)
        coVerify(exactly = 0) { useCases.addProduct(any()) }
    }

    @Ignore("stateflow vs livedata")
    @Test
    fun getProductSuccess() = runTest {
        // given
        val product = Product("Test", "Description", 0, 0, 123L, "https://example.com")
        coEvery { useCases.getProduct(123L) } returns product

        // when
        val liveData = viewModel.getProduct(123L)
        var observedProduct: Product? = null
        liveData.observeForever {
            observedProduct = it
        }
        advanceUntilIdle()

        // then
        assertEquals(product, observedProduct)
    }
}