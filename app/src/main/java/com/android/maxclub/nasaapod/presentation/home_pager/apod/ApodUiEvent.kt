package com.android.maxclub.nasaapod.presentation.home_pager.apod

import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.util.ApodDate
import com.android.maxclub.nasaapod.data.util.ImageInfo

sealed class ApodUiEvent {
    data class OnDateLoaded(val apodDate: ApodDate.From) : ApodUiEvent()
    data class OnShowError(val exception: Throwable) : ApodUiEvent()
    data class OnImageOpen(val imageInfo: ImageInfo) : ApodUiEvent()
    object OnShowImageError : ApodUiEvent()
    data class OnVideoOpen(val videoUrl: String) : ApodUiEvent()
    object OnShowVideoError : ApodUiEvent()
    data class OnShare(val apod: Apod) : ApodUiEvent()
    object OnShowShareError : ApodUiEvent()
}