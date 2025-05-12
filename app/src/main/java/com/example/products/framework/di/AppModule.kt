package com.example.products.framework.di

import android.content.Context
import androidx.room.Room
import com.example.products.core.repository.ProductDataSource
import com.example.products.core.repository.ProductRepository
import com.example.products.core.usecase.AddProduct
import com.example.products.core.usecase.GetAllProducts
import com.example.products.framework.repository.ProductDataSourceImpl
import com.example.products.core.usecase.UseCases
import com.example.products.framework.db.ProductDao
import com.example.products.framework.db.ProductDatabase
import com.example.products.framework.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindProductDataSource(
        impl: ProductDataSourceImpl
    ): ProductDataSource

    companion object {
        @Provides
        @Singleton
        fun provideProductDatabase(
            @ApplicationContext appContext: Context
        ): ProductDatabase {
            return Room.databaseBuilder(
                appContext,
                ProductDatabase::class.java,
                "products.db")
                .fallbackToDestructiveMigration() // for schema version incrementation
                .build()
        }

        @Provides
        fun provideProductDao(db: ProductDatabase): ProductDao = db.productDao()

        @Provides
        fun provideUseCases(repository: ProductRepository): UseCases {
            return UseCases(
                addProduct = AddProduct(repository),
                getAllProducts = GetAllProducts(repository),
                getProduct = { id -> repository.getProductById(id)},
                syncProducts = { repository.syncProducts() }
            )
        }

    }
}
