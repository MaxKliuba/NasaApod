package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.data.*
import com.android.maxclub.nasaapod.data.repository.ApodRepository
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.uistates.ApodUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteApodViewModel @Inject constructor(
    private val apodRepository: ApodRepository,
    private val favoriteApodRepository: FavoriteApodRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApodUiState>(ApodUiState.Initializing)
    val uiState: StateFlow<ApodUiState> = _uiState
    private var fetchJob: Job? = null
    private var favoritesJob: Job? = null
    lateinit var currentFavoriteApod: FavoriteApod
    val currentApod: Apod
        get() = (uiState.value as? ApodUiState.Success)?.apod
            ?: uiState.value.lastApod
            ?: currentFavoriteApod.toApod()

    fun fetchCurrentApod() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            apodRepository.getApodByDate(currentApod.date)
                .onStart {
                    _uiState.value = ApodUiState.Loading(_uiState.value.lastApod)
                }.catch { exception ->
                    _uiState.value = ApodUiState.Error(exception, _uiState.value.lastApod)
                }.collect { apod ->
                    _uiState.value = ApodUiState.Success(apod)
                }
        }
    }

    fun removeFavoriteApod(favoriteApod: FavoriteApod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            favoriteApodRepository.removeFavoriteApod(favoriteApod)
        }
    }
}