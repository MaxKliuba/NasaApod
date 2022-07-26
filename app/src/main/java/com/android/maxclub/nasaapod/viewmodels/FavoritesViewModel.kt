package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.uistates.FavoritesViewPagerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<FavoritesViewPagerUiState>(FavoritesViewPagerUiState.Initializing)
    val uiState: StateFlow<FavoritesViewPagerUiState> = _uiState
    var currentPosition: Int = 0

    init {
        viewModelScope.launch {
            launch {
                favoriteApodRepository.getFavoriteApods()
                    .collect { favoriteApods ->
                        _uiState.value = FavoritesViewPagerUiState.DataChanged(favoriteApods)
                    }
            }
        }
    }

    fun unmarkAsNewFavoriteApod(favoriteApod: FavoriteApod) =
        viewModelScope.launch {
            favoriteApodRepository.updateFavoriteApod(favoriteApod.copy(isNew = false))
        }
}