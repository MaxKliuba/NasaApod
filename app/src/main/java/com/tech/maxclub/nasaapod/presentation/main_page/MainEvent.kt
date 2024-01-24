package com.tech.maxclub.nasaapod.presentation.main_page

sealed class MainEvent {
    object OnNavigationIconClick : MainEvent()
    data class OnDestinationChanged(val destinationId: Int) : MainEvent()
    data class OnTabChanged(val tabIndex: Int) : MainEvent()
    data class OnTabSelected(val tabIndex: Int) : MainEvent()
    data class OnTabReselected(val tabIndex: Int) : MainEvent()
}