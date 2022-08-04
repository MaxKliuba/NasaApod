package com.android.maxclub.nasaapod.data.source.remote.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApodInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newUrl = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", ApodService.API_KEY)
            .build()

        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}