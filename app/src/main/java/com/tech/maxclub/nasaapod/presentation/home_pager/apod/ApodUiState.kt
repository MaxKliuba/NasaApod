package com.tech.maxclub.nasaapod.presentation.home_pager.apod

import com.tech.maxclub.nasaapod.domain.model.Apod

sealed class ApodUiState(val cachedApod: Apod?) {
    class Loading(cachedApod: Apod? = null) : ApodUiState(cachedApod)
    class Success(val apod: Apod) : ApodUiState(apod)
    class Error(val exception: Throwable, cachedApod: Apod? = null) : ApodUiState(cachedApod)
}