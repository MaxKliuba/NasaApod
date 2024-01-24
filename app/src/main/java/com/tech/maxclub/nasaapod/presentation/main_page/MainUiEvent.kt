package com.tech.maxclub.nasaapod.presentation.main_page

sealed class MainUiEvent {
    object OnNavigationIconClick : MainUiEvent()
    data class OnDestinationChanged(val destinationId: Int) : MainUiEvent()
    data class OnTabSelected(val tabIndex: Int) : MainUiEvent()
    data class OnTabReselected(val tabIndex: Int) : MainUiEvent()
}