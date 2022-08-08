package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

import com.android.maxclub.nasaapod.domain.model.Apod

sealed class FavoriteApodUiState(val cachedApod: Apod?) {
    class Loading(cachedApod: Apod? = null) : FavoriteApodUiState(cachedApod)
    class Success(val apod: Apod) : FavoriteApodUiState(apod)
    class Error(val exception: Throwable, cachedApod: Apod? = null) :
        FavoriteApodUiState(cachedApod)
}
