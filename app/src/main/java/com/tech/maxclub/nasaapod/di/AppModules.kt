package com.tech.maxclub.nasaapod.di

import android.content.Context
import com.tech.maxclub.nasaapod.data.remote.api.ApodService
import com.tech.maxclub.nasaapod.data.local.database.FavoriteApodDao
import com.tech.maxclub.nasaapod.data.local.database.FavoriteApodDatabase
import com.tech.maxclub.nasaapod.data.local.FavoriteApodLocalDataSource
import com.tech.maxclub.nasaapod.data.local.FavoriteApodLocalDataSourceImpl
import com.tech.maxclub.nasaapod.data.remote.ApodRemoteDataSource
import com.tech.maxclub.nasaapod.data.remote.ApodRemoteDataSourceImpl
import com.tech.maxclub.nasaapod.data.repository.ApodRepositoryImpl
import com.tech.maxclub.nasaapod.data.repository.FavoriteApodRepositoryImpl
import com.tech.maxclub.nasaapod.domain.repository.ApodRepository
import com.tech.maxclub.nasaapod.domain.repository.FavoriteApodRepository
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
object DatabaseModule {

    @Provides
    fun provideFavoriteApodDao(database: FavoriteApodDatabase): FavoriteApodDao =
        database.favoriteApodDao()

    @Singleton
    @Provides
    fun provideFavoriteApodDatabase(@ApplicationContext context: Context): FavoriteApodDatabase =
        FavoriteApodDatabase.create(context)
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
object RepositoryModule {

    @Singleton
    @Provides
    fun provideApodRepository(
        apodRemoteDataSource: ApodRemoteDataSource,
        favoriteApodLocalDataSource: FavoriteApodLocalDataSource,
    ): ApodRepository =
        ApodRepositoryImpl(apodRemoteDataSource, favoriteApodLocalDataSource)

    @Singleton
    @Provides
    fun provideFavoriteApodRepository(
        favoriteApodLocalDataSource: FavoriteApodLocalDataSource
    ): FavoriteApodRepository =
        FavoriteApodRepositoryImpl(favoriteApodLocalDataSource)
}