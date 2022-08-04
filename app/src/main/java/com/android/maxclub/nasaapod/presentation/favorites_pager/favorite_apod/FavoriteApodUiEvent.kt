package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.util.ImageInfo

sealed class FavoriteApodUiEvent {
    data class OnShowError(val exception: Throwable) : FavoriteApodUiEvent()
    data class OnImageOpen(val imageInfo: ImageInfo) : FavoriteApodUiEvent()
    object OnShowImageError : FavoriteApodUiEvent()
    data class OnVideoOpen(val videoUrl: String) : FavoriteApodUiEvent()
    object OnShowVideoError : FavoriteApodUiEvent()
    data class OnShare(val apod: Apod) : FavoriteApodUiEvent()
    data class OnDelete(val favoriteApod: FavoriteApod) : FavoriteApodUiEvent()
}