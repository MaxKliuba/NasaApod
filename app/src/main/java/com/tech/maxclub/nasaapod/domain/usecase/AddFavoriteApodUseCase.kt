package com.tech.maxclub.nasaapod.domain.usecase

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod
import com.tech.maxclub.nasaapod.domain.repository.FavoriteApodRepository
import javax.inject.Inject

class AddFavoriteApodUseCase @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) {
    suspend operator fun invoke(favoriteApod: FavoriteApod) =
        favoriteApodRepository.addFavoriteApod(favoriteApod)
}