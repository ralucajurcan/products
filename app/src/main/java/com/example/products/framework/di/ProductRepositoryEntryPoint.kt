package com.example.products.framework.di

import com.example.products.core.repository.ProductRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProductRepositoryEntryPoint {
    fun productRepository(): ProductRepository
}