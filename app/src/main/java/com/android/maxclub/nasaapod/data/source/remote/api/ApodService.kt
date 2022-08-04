package com.android.maxclub.nasaapod.data.source.remote.api

import com.android.maxclub.nasaapod.data.source.remote.ApodDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodService {
    @GET("planetary/apod")
    suspend fun getApodOfToday(): ApodDto

    @GET("planetary/apod")
    suspend fun getApodByDate(@Query("date") date: String): ApodDto

    @GET("planetary/apod?count=1")
    suspend fun getRandomApod(): List<ApodDto>

    companion object {
        private const val BASE_URL = "https://api.nasa.gov/"
        const val API_KEY = "DG8RfuLFTDzZUWF8BDPlJR5PmIIGKjHVMN5Szc06"

        @OptIn(ExperimentalSerializationApi::class)
        fun create(): ApodService {
            val client = OkHttpClient.Builder()
                .addInterceptor(ApodInterceptor())
                .build()

            val contentType = MediaType.get("application/json")
            val json = Json {
                ignoreUnknownKeys = true
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
                .create(ApodService::class.java)
        }
    }
}