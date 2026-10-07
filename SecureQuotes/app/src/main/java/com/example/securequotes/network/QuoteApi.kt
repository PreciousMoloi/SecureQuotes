package com.example.securequotes.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/** Response from https://dummyjson.com/quotes/random (public REST API, no key needed). */
data class Quote(val id: Int, val quote: String, val author: String)

interface QuoteApi {
    @GET("quotes/random")
    suspend fun randomQuote(): Quote
}

object ApiClient {
    val api: QuoteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }
}
