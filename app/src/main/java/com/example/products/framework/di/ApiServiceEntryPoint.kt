package com.example.products.framework.di

import com.example.products.framework.remote.ApiService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// manually access a dependency outside of the usual constructor injection
// in SyncProductsWorker -- normal Hilt injection doesn't work directly
@EntryPoint
@InstallIn(SingletonComponent::class) // makes the dependency available for the lifetime of the application
interface ApiServiceEntryPoint {
    fun apiService() : ApiService
}