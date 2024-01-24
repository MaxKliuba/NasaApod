package com.tech.maxclub.nasaapod.presentation.main_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.nasaapod.domain.usecase.GetFavoriteApodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getFavoriteApods: GetFavoriteApodsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState(0, 0))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MainUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchFavoriteApods()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnNavigationIconClick -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnNavigationIconClick)
                }
            }
            is MainEvent.OnDestinationChanged -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnDestinationChanged(event.destinationId))
                }
            }
            is MainEvent.OnTabChanged -> {
                _uiState.value = _uiState.value.copy(currentTabIndex = event.tabIndex)
            }
            is MainEvent.OnTabSelected -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnTabSelected(event.tabIndex))
                }
            }
            is MainEvent.OnTabReselected -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainUiEvent.OnTabReselected(event.tabIndex))
                }
            }
        }
    }

    private fun fetchFavoriteApods() {
        viewModelScope.launch {
            getFavoriteApods()
                .map { favorites ->
                    favorites.count { it.isNew }
                }.collect { count ->
                    _uiState.value = _uiState.value.copy(badgeValue = count)
                }
        }
    }
}