package com.android.maxclub.nasaapod.uistates

import com.android.maxclub.nasaapod.data.Apod

sealed class ApodUiState {
    object Initializing : ApodUiState()
    object Loading : ApodUiState()
    class Success(val data: Apod) : ApodUiState()
    class Error(val exception: Throwable) : ApodUiState()
}
