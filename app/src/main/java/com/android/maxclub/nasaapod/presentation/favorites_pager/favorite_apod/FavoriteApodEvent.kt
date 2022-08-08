package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

import com.android.maxclub.nasaapod.domain.model.Apod

sealed class FavoriteApodEvent {
    object OnRefresh : FavoriteApodEvent()
    data class OnShowData(val apod: Apod) : FavoriteApodEvent()
    data class OnError(val exception: Throwable) : FavoriteApodEvent()
    object OnImageClick : FavoriteApodEvent()
    object OnVideoClick : FavoriteApodEvent()
    object OnShareButtonClick : FavoriteApodEvent()
    object OnDelete : FavoriteApodEvent()
}