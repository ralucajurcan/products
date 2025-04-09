package com.example.products.framework.remote

import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun fetchFakeProducts(): FakeProductResponse

}