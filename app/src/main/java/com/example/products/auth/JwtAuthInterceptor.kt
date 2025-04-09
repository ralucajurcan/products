package com.example.products.auth

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class JwtAuthInterceptor @Inject constructor(
    private val jwtManager: JwtManager
) : Interceptor {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // runBlocking is used because OkHttp Interceptor is synchronous
        // interceptors are not suspendable
        runBlocking {
            jwtManager.getDecodedToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            response.close()

            val newToken = runBlocking {
                jwtManager.refreshToken()
            }

            return run {
                runBlocking {
                    jwtManager.saveToken(newToken)
                }

                val newRequest: Request = chain.request().newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()

                chain.proceed(newRequest)
            }
        }

        return response
    }
}
