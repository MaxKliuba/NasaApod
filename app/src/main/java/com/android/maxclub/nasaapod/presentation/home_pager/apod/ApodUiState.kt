package com.android.maxclub.nasaapod.presentation.home_pager.apod

import com.android.maxclub.nasaapod.data.Apod

sealed class ApodUiState(val cachedApod: Apod?) {
    class Loading(cachedApod: Apod? = null) : ApodUiState(cachedApod)
    class Success(val apod: Apod) : ApodUiState(apod)
    class Error(val exception: Throwable, cachedApod: Apod? = null) : ApodUiState(cachedApod)
}
