package com.magicbluepenguin.testapplication.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitServiceProvider(val authHeader: String) {

    private val retrofit by lazy {
        val defaultHttpClient = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {

                override fun intercept(chain: Interceptor.Chain): Response {
                    val authorisedRequest = chain.request().newBuilder()
                        .addHeader("Authorization", authHeader).build()
                    return chain.proceed(authorisedRequest)
                }
            }).build()

        Retrofit.Builder()
            .client(defaultHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://marlove.net/e/mock/v1/")
            .build()
    }

    val itemService = retrofit.create<ItemService>(ItemService::class.java)
}