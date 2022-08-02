package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.uistates.FavoriteListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoriteListUiState>(FavoriteListUiState.Initializing)
    val uiState: StateFlow<FavoriteListUiState> = _uiState
    private var fetchJob: Job? = null

    fun fetchFavoriteApods() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            favoriteApodRepository.getFavoriteApods()
                .onStart {
                    _uiState.value = FavoriteListUiState.Loading(_uiState.value.favoriteApods)
                }.catch {
                    _uiState.value = FavoriteListUiState.DataChanged(_uiState.value.favoriteApods)
                }.collect { favoriteApods ->
                    _uiState.value = FavoriteListUiState.DataChanged(favoriteApods)
                }
        }
    }

    fun updateUiStateFavoriteApods(favoriteApods: List<FavoriteApod>) {
        _uiState.value = FavoriteListUiState.DataChanged(favoriteApods)
    }

    fun updateFavoriteApods(vararg favoriteApods: FavoriteApod) =
        viewModelScope.launch {
            favoriteApodRepository.updateFavoriteApods(*favoriteApods)
        }
}