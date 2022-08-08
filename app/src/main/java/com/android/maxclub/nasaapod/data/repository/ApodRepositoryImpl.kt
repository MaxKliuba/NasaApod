package com.android.maxclub.nasaapod.data.repository

import com.android.maxclub.nasaapod.domain.model.Apod
import com.android.maxclub.nasaapod.data.local.FavoriteApodLocalDataSource
import com.android.maxclub.nasaapod.data.remote.dto.ApodDto
import com.android.maxclub.nasaapod.data.remote.ApodRemoteDataSource
import com.android.maxclub.nasaapod.domain.util.toApod
import com.android.maxclub.nasaapod.domain.repository.ApodRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class ApodRepositoryImpl @Inject constructor(
    private val apodRemoteDataSource: ApodRemoteDataSource,
    private val favoriteApodLocalDataSource: FavoriteApodLocalDataSource,
) : ApodRepository {
    private val cachedApods: MutableSet<ApodDto> = mutableSetOf()

    override fun getApodOfToday(date: Date?): Flow<Apod> =
        (date?.let { tryGetApodDtoByDate(it) } ?: apodRemoteDataSource.getApodOfToday())
            .cache()
            .toApodFlow()

    override fun getApodByDate(date: Date): Flow<Apod> =
        (tryGetApodDtoByDate(date) ?: apodRemoteDataSource.getApodByDate(date))
            .cache()
            .toApodFlow()

    override fun getRandomApod(): Flow<Apod> =
        apodRemoteDataSource.getRandomApod()
            .cache()
            .toApodFlow()

    private fun tryGetApodDtoByDate(date: Date): Flow<ApodDto>? =
        cachedApods.find { cachedApod ->
            cachedApod.date == date
        }?.let { cachedApod ->
            flow { emit(cachedApod) }
        }

    private fun Flow<ApodDto>.cache(): Flow<ApodDto> =
        onEach { apodDto ->
            cachedApods += apodDto
        }

    private fun Flow<ApodDto>.toApodFlow(): Flow<Apod> =
        map { apodDto ->
            apodDto.toApod(
                isFavorite = favoriteApodLocalDataSource.getFavoriteApodByDate(apodDto.date)
                    .firstOrNull() != null
            )
        }
}