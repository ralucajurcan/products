package com.example.products.framework.di

import com.example.products.BuildConfig
import com.example.products.auth.JwtAuthInterceptor
import com.example.products.framework.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = BuildConfig.BASE_URL

    // builds the HTTP client; different interceptors can be plugged-in (logging, JWT etc)
    @Provides
    @Singleton
    fun provideOkHttpClient(jwtAuthInterceptor: JwtAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(jwtAuthInterceptor)
            .build()
    }

    // provides the library that turns HTTP API into a Kotlin interface; depends on the OkHttpClient
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // turns the retrofit instance into a concrete implementation of the ApiService interface
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}