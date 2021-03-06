package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.data.*
import com.android.maxclub.nasaapod.data.repository.ApodDateRepository
import com.android.maxclub.nasaapod.data.repository.ApodRepository
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.uistates.ApodUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ApodViewModel @Inject constructor(
    private val apodDateRepository: ApodDateRepository,
    private val apodRepository: ApodRepository,
    private val favoriteApodRepository: FavoriteApodRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApodUiState>(ApodUiState.Initializing)
    val uiState: StateFlow<ApodUiState> = _uiState
    private var fetchJob: Job? = null
    private var favoritesJob: Job? = null
    var currentApodDate: ApodDate = ApodDate.Today()
    val currentApod: Apod? get() = (uiState.value as? ApodUiState.Success)?.data

    fun fetchInitApod() {
        currentApodDate.let { apodDate ->
            when (apodDate) {
                is ApodDate.Today -> fetchApodOfToday()
                is ApodDate.From -> fetchApodByDate(apodDate.date!!)
                is ApodDate.Random -> fetchRandomApod()
            }
        }
    }

    private fun fetchApodOfToday() {
        fetchApod(apodRepository.getApodOfToday())
    }

    private fun fetchApodByDate(date: Date) {
        fetchApod(apodRepository.getApodByDate(date))
    }

    private fun fetchRandomApod() {
        fetchApod(apodRepository.getRandomApod())
    }

    fun refreshCurrentApod() {
        currentApodDate.let { apodDate ->
            when {
                apodDate is ApodDate.Today -> fetchApodOfToday()
                apodDate.date != null -> fetchApodByDate(apodDate.date)
                else -> fetchRandomApod()
            }
        }
    }

    fun addToFavorites(apod: Apod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            if (favoriteApodRepository.addApodToFavorites(apod)) {
                _uiState.value = ApodUiState.Success(apod.copy(isFavorite = true))
            }
        }
    }

    fun removeFromFavorites(apod: Apod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            if (favoriteApodRepository.removeApodFromFavorites(apod)) {
                _uiState.value = ApodUiState.Success(apod.copy(isFavorite = false))
            }
        }
    }

    private fun fetchApod(apodFlow: Flow<Apod>) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            apodFlow
                .onStart {
                    _uiState.value = ApodUiState.Loading
                }.catch { exception ->
                    _uiState.value = ApodUiState.Error(exception)
                }.collect { apod ->
                    currentApodDate = ApodDate.From(apod.date, currentApodDate.id)
                    apodDateRepository.updateApodDate(currentApodDate)
                    apodDateRepository.updateLastLoadedDate(apod.date)
                    _uiState.value = ApodUiState.Success(apod)
                }
        }
    }
}