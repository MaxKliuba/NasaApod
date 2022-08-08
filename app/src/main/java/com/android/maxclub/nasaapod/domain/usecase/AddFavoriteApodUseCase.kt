package com.android.maxclub.nasaapod.domain.usecase

import com.android.maxclub.nasaapod.domain.model.FavoriteApod
import com.android.maxclub.nasaapod.domain.repository.FavoriteApodRepository
import javax.inject.Inject

class AddFavoriteApodUseCase @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) {
    suspend operator fun invoke(favoriteApod: FavoriteApod) =
        favoriteApodRepository.addFavoriteApod(favoriteApod)
}