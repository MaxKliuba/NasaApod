package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.uistates.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<FavoritesUiState>(FavoritesUiState.Initializing)
    val uiState: StateFlow<FavoritesUiState> = _uiState
    var currentPosition: Int = 0

    init {
        viewModelScope.launch {
            launch {
                favoriteApodRepository.getFavoriteApods()
                    .catch {
                        _uiState.value = FavoritesUiState.DataChanged(_uiState.value.favoriteApods)
                    }.collect { favoriteApods ->
                        _uiState.value = FavoritesUiState.DataChanged(favoriteApods)
                    }
            }
        }
    }

    fun unmarkAsNewFavoriteApod(favoriteApod: FavoriteApod) =
        viewModelScope.launch {
            favoriteApodRepository.updateFavoriteApods(favoriteApod.copy(isNew = false))
        }
}