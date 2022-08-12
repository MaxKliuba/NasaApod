package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

sealed class FavoriteApodEvent {
    object OnRefresh : FavoriteApodEvent()
    object OnShowData : FavoriteApodEvent()
    data class OnError(val exception: Throwable) : FavoriteApodEvent()
    object OnImageClick : FavoriteApodEvent()
    object OnVideoClick : FavoriteApodEvent()
    object OnShareButtonClick : FavoriteApodEvent()
    object OnDelete : FavoriteApodEvent()
}