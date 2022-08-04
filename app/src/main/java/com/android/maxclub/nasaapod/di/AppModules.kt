package com.android.maxclub.nasaapod.di

import android.content.Context
import com.android.maxclub.nasaapod.data.source.remote.api.ApodService
import com.android.maxclub.nasaapod.data.source.local.FavoriteApodDao
import com.android.maxclub.nasaapod.data.source.local.FavoriteApodDatabase
import com.android.maxclub.nasaapod.data.source.local.FavoriteApodLocalDataSourceImpl
import com.android.maxclub.nasaapod.data.source.local.FavoriteApodLocalDataSource
import com.android.maxclub.nasaapod.data.source.remote.ApodRemoteDataSourceImpl
import com.android.maxclub.nasaapod.data.source.remote.ApodRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    ): ApodRemoteDataSource =
        ApodRemoteDataSourceImpl(apodService)

    @Provides
    fun provideFavoriteApodLocalDataSource(
        favoriteApodDao: FavoriteApodDao
    ): FavoriteApodLocalDataSource =
        FavoriteApodLocalDataSourceImpl(favoriteApodDao)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideFavoriteApodDao(database: FavoriteApodDatabase): FavoriteApodDao =
        database.favoriteApodDao()

    @Singleton
    @Provides
    fun provideFavoriteApodDatabase(@ApplicationContext context: Context): FavoriteApodDatabase =
        FavoriteApodDatabase.create(context)
}