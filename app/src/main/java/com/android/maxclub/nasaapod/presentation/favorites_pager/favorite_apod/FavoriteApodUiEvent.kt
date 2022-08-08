package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

import com.android.maxclub.nasaapod.domain.model.Apod
import com.android.maxclub.nasaapod.domain.model.FavoriteApod
import com.android.maxclub.nasaapod.domain.model.ImageInfo

sealed class FavoriteApodUiEvent {
    data class OnShowError(val exception: Throwable) : FavoriteApodUiEvent()
    data class OnImageOpen(val imageInfo: ImageInfo) : FavoriteApodUiEvent()
    object OnShowImageError : FavoriteApodUiEvent()
    data class OnVideoOpen(val videoUrl: String) : FavoriteApodUiEvent()
    object OnShowVideoError : FavoriteApodUiEvent()
    data class OnShare(val apod: Apod) : FavoriteApodUiEvent()
    data class OnDelete(val favoriteApod: FavoriteApod) : FavoriteApodUiEvent()
}