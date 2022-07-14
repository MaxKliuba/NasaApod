package com.android.maxclub.nasaapod.di

import com.android.maxclub.nasaapod.api.ApodService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApodService(): ApodService =
        ApodService.create()
}