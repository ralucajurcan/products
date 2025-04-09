package com.example.products.framework.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.products.common.model.Product
import com.example.products.framework.db.ProductDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProductDataSourceImplTest {

    private lateinit var context: Context
    private lateinit var database: ProductDatabase
    private lateinit var dataSource: ProductDataSourceImpl

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context, // Room needs a context to function - this provides a test context
            ProductDatabase::class.java
        ).allowMainThreadQueries() // disables running on the main thread for unit tests
            .build()

        dataSource = ProductDataSourceImpl(database.productDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testDataSourceImpl_success() = runTest {
        // given
        val product = Product(
            title = "Test Product",
            description = "This is a test",
            creationTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            imageUrl = "https://example.com/image.jpg"
        )
        dataSource.addProduct(product)

        // when
        val result = dataSource.getAllProducts()

        // then
        assertNotNull(result)
        val products = dataSource.getAllProducts().first()
        assertEquals(1, products.size)

        val productRes = products.first()
        assertEquals(product.title, productRes.title)
        assertEquals(product.description, productRes.description)
    }

}