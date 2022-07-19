package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.ApodRepository
import com.android.maxclub.nasaapod.uistates.ApodUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ApodViewModel @Inject constructor(
    private val apodRepository: ApodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApodUiState>(ApodUiState.Initializing)
    val uiState: StateFlow<ApodUiState> = _uiState
    private var fetchJob: Job? = null
    private var favoritesJob: Job? = null

    var isDateRandom: Boolean = false
        private set
    var currentDate: Date? = null
        private set
    val currentApod: Apod? get() = (uiState.value as? ApodUiState.Success)?.data
    var isImageLoaded: Boolean
        get() = currentApod?.isImageLoaded ?: false
        set(value) {
            currentApod?.isImageLoaded = value
        }

    fun setArguments(isDateRandom: Boolean, date: Date? = null) {
        this.isDateRandom = isDateRandom
        currentDate = date
    }

    fun fetchInitApod() {
        if (isDateRandom) {
            fetchRandomApod()
        } else {
            currentDate?.let { date ->
                fetchApodByDate(date)
            } ?: fetchApodOfToday()
        }
    }

    fun fetchApodOfToday() {
        fetchApod(apodRepository.getApodOfToday())
    }

    fun fetchApodByDate(date: Date) {
        fetchApod(apodRepository.getApodByDate(date))
    }

    fun fetchRandomApod() {
        fetchApod(apodRepository.getRandomApod())
    }

    fun refreshCurrentApod() {
        currentDate?.let { date ->
            fetchApod(apodRepository.getApodByDate(date))
        } ?: fetchRandomApod()
    }

    fun addToFavorites(apod: Apod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            apodRepository.addApodToFavorites(apod)
                .collect { newApod ->
                    _uiState.value = ApodUiState.Success(newApod)
                }
        }
    }

    fun removeFromFavorites(apod: Apod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            apodRepository.removeApodFromFavorites(apod)
                .collect { newApod ->
                    _uiState.value = ApodUiState.Success(newApod)
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
                    currentDate = apod.date
                    _uiState.value = ApodUiState.Success(apod)
                }
        }
    }
}