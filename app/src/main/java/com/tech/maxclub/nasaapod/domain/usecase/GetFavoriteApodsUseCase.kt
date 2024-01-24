package com.tech.maxclub.nasaapod.domain.usecase

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod
import com.tech.maxclub.nasaapod.domain.repository.FavoriteApodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteApodsUseCase @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) {
    operator fun invoke(): Flow<List<FavoriteApod>> =
        favoriteApodRepository.getFavoriteApods()
            .map { favoriteApods ->
                favoriteApods.sortedByDescending { it.position }
            }
}