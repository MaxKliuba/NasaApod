package com.tech.maxclub.nasaapod.presentation.home_pager.apod_pager

import com.tech.maxclub.nasaapod.domain.model.ApodDate

data class HomeUiState(
    val apodDates: List<ApodDate>,
)