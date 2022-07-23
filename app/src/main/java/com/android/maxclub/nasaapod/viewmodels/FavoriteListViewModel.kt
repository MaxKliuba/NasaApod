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
                    _uiState.value = FavoriteListUiState.Loading
                }.catch { exception ->
                    _uiState.value = FavoriteListUiState.Error(exception)
                }.collect { favoriteApods ->
                    _uiState.value = FavoriteListUiState.Success(favoriteApods)
                }
        }
    }

    fun unmarkAsNewFavoriteApod(favoriteApod: FavoriteApod) =
        updateFavoriteApod(
            favoriteApod.copy(isNew = false)
        )

    private fun updateFavoriteApod(favoriteApod: FavoriteApod) =
        viewModelScope.launch {
            favoriteApodRepository.updateFavoriteApod(favoriteApod)
        }
}