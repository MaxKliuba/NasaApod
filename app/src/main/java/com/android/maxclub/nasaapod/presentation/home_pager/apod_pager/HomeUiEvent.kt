package com.android.maxclub.nasaapod.presentation.home_pager.apod_pager

import java.util.*

sealed class HomeUiEvent {
    data class OnShowDatePicker(
        val startDate: Date,
        val endDate: Date,
        val isValid: (Date) -> Boolean,
    ) : HomeUiEvent()
}
