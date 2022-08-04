package com.android.maxclub.nasaapod.presentation.home_pager.apod

sealed class ApodEvent {
    object OnRefresh : ApodEvent()
    data class OnError(val exception: Throwable) : ApodEvent()
    object OnFavoritesButtonClick : ApodEvent()
    object OnImageClick : ApodEvent()
    object OnVideoClick : ApodEvent()
    object OnShareButtonClick : ApodEvent()
}