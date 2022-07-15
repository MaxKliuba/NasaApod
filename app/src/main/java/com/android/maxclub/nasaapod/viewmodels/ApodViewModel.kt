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

    val currentApod: Apod? get() = (uiState.value as? ApodUiState.Success)?.data
    var isImageLoaded: Boolean
        get() = currentApod?.isImageLoaded ?: false
        set(value) {
            currentApod?.isImageLoaded = value
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
        currentApod?.let { apod ->
            fetchApod(apodRepository.getApodByDate(apod.date))
        }
    }

    fun addToFavorite(apod: Apod) {
        // TODO
    }

    fun removeFromFavorite(apod: Apod) {
        // TODO
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
                    _uiState.value = ApodUiState.Success(apod)
                }
        }
    }
}