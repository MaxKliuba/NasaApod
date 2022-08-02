package com.android.maxclub.nasaapod.uistates

import com.android.maxclub.nasaapod.data.Apod

sealed class ApodUiState(val lastApod: Apod?) {
    object Initializing : ApodUiState(null)
    class Loading(lastApod: Apod?) : ApodUiState(lastApod)
    class Success(val apod: Apod) : ApodUiState(apod)
    class Error(val exception: Throwable, lastApod: Apod?) : ApodUiState(lastApod)
}
