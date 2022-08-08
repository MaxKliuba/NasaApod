package com.android.maxclub.nasaapod.domain.usecase

import javax.inject.Inject

data class ApodUseCases @Inject constructor(
    val getApod: GetApodUseCase,
    val getFavoriteApods: GetFavoriteApodsUseCase,
    val addFavoriteApod: AddFavoriteApodUseCase,
    val deleteFavoriteApod: DeleteFavoriteApodUseCase,
    val updateFavoriteApods: UpdateFavoriteApodsUseCase,
)
