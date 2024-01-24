package com.tech.maxclub.nasaapod.domain.usecase

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod
import com.tech.maxclub.nasaapod.domain.repository.FavoriteApodRepository
import javax.inject.Inject

class DeleteFavoriteApodUseCase @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) {
    suspend operator fun invoke(favoriteApod: FavoriteApod) =
        favoriteApodRepository.deleteFavoriteApod(favoriteApod)
}