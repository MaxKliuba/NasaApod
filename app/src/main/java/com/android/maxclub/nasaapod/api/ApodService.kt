package com.android.maxclub.nasaapod.api

import com.android.maxclub.nasaapod.data.Apod
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodService {
    @GET("planetary/apod")
    suspend fun getApod(): Response<Apod>

    @GET("planetary/apod")
    suspend fun getApodByDate(@Query("date") date: String): Response<Apod>

    @GET("planetary/apod")
    suspend fun getRandomApod(@Query("count") count: Int = 1): Response<List<Apod>>

    companion object {
        private const val BASE_URL = "https://api.nasa.gov/"
        const val API_KEY = "DG8RfuLFTDzZUWF8BDPlJR5PmIIGKjHVMN5Szc06"

        fun create(): ApodService {
            val client = OkHttpClient.Builder()
                .addInterceptor(ApodInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApodService::class.java)
        }
    }
}