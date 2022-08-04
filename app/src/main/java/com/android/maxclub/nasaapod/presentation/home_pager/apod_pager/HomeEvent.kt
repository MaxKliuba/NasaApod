package com.android.maxclub.nasaapod.presentation.home_pager.apod_pager

import com.android.maxclub.nasaapod.data.util.ApodDate
import java.util.*

sealed class HomeEvent {
    data class OnPositionChanged(val position: Int) : HomeEvent()
    data class OnItemLoaded(val apodDate: ApodDate.From) : HomeEvent()
    object OnStateReset : HomeEvent()
    object OnRandomDateClick : HomeEvent()
    object OnDatePickerClick : HomeEvent()
    data class OnDateSelected(val date: Date) : HomeEvent()
}
