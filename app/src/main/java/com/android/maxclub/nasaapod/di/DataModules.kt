package com.android.maxclub.nasaapod.di

import com.android.maxclub.nasaapod.api.ApodService
import com.android.maxclub.nasaapod.data.source.remote.ApodRemoteDataSource
import com.android.maxclub.nasaapod.data.source.remote.IApodRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideApodService(): ApodService =
        ApodService.create()
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun provideApodRemoteDataSource(
        apodService: ApodService
    ): IApodRemoteDataSource =
        ApodRemoteDataSource(apodService)
}