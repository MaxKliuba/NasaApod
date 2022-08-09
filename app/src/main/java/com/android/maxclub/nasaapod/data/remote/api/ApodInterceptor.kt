package com.android.maxclub.nasaapod.data.remote.api

import com.android.maxclub.nasaapod.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApodInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newUrl = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()

        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}