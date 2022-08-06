package com.android.maxclub.nasaapod.presentation.home_pager.apod

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.data.*
import com.android.maxclub.nasaapod.data.repository.ApodRepository
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.data.util.ApodDate
import com.android.maxclub.nasaapod.data.util.MediaType
import com.android.maxclub.nasaapod.data.util.toFavoriteApod
import com.android.maxclub.nasaapod.data.util.toImageInfo
import com.android.maxclub.nasaapod.presentation.home_pager.apod.ApodFragment.Companion.ARG_APOD_DATE
import com.android.maxclub.nasaapod.util.ServiceDateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ApodViewModel @Inject constructor(
    private val apodRepository: ApodRepository,
    private val favoriteApodRepository: FavoriteApodRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApodUiState>(ApodUiState.Loading())
    val uiState: StateFlow<ApodUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<ApodUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var fetchJob: Job? = null
    private var favoritesJob: Job? = null

    private var apodDate: ApodDate = savedStateHandle[ARG_APOD_DATE] ?: ApodDate.Today()
    private val currentApod: Apod?
        get() = (uiState.value as? ApodUiState.Success)?.apod ?: uiState.value.cachedApod

    init {
        fetchCurrentApod()
        fetchFavoriteApods()
    }

    fun onEvent(event: ApodEvent) {
        when (event) {
            is ApodEvent.OnRefresh -> {
                fetchCurrentApod()
            }
            is ApodEvent.OnError -> {
                viewModelScope.launch {
                    _uiEvent.emit(ApodUiEvent.OnShowError(event.exception))
                }
            }
            is ApodEvent.OnFavoritesButtonClick -> {
                viewModelScope.launch {
                    currentApod?.let { apod ->
                        if (apod.isFavorite) removeFromFavorites(apod) else addToFavorites(apod)
                    }
                }
            }
            is ApodEvent.OnImageClick -> {
                viewModelScope.launch {
                    currentApod.let { apod ->
                        if (apod != null && apod.mediaType == MediaType.IMAGE) {
                            _uiEvent.emit(ApodUiEvent.OnImageOpen(apod.toImageInfo()))
                        } else {
                            _uiEvent.emit(ApodUiEvent.OnShowImageError)
                        }
                    }
                }
            }
            is ApodEvent.OnVideoClick -> {
                viewModelScope.launch {
                    currentApod.let { apod ->
                        if (apod != null && apod.mediaType == MediaType.VIDEO) {
                            _uiEvent.emit(ApodUiEvent.OnVideoOpen(apod.url))
                        } else {
                            _uiEvent.emit(ApodUiEvent.OnShowVideoError)
                        }
                    }
                }
            }
            is ApodEvent.OnShareButtonClick -> {
                viewModelScope.launch {
                    currentApod?.let { apod ->
                        _uiEvent.emit(ApodUiEvent.OnShare(apod))
                    } ?: _uiEvent.emit(ApodUiEvent.OnShowShareError)
                }
            }
        }
    }

    private fun fetchCurrentApod() {
        apodDate.let { apodDate ->
            when (apodDate) {
                is ApodDate.Today -> fetchApodOfToday(ServiceDateManager.getTodayDate())
                is ApodDate.From -> fetchApodByDate(apodDate.date)
                is ApodDate.Random -> fetchRandomApod()
            }
        }
    }

    private fun fetchApodOfToday(date: Date? = null) {
        fetchApod(apodRepository.getApodOfToday(date))
    }

    private fun fetchApodByDate(date: Date) {
        fetchApod(apodRepository.getApodByDate(date))
    }

    private fun fetchRandomApod() {
        fetchApod(apodRepository.getRandomApod())
    }

    private fun fetchApod(apodFlow: Flow<Apod>) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            apodFlow
                .onStart {
                    _uiState.value = ApodUiState.Loading(_uiState.value.cachedApod)
                }.catch { exception ->
                    _uiState.value = ApodUiState.Error(exception, _uiState.value.cachedApod)
                }.collect { apod ->
                    val newApodDate = ApodDate.From(apod.date, apodDate.id)
                    apodDate = newApodDate
                    _uiEvent.emit(ApodUiEvent.OnDateLoaded(newApodDate))
                    _uiState.value = ApodUiState.Success(apod)
                }
        }
    }

    private fun fetchFavoriteApods() {
        viewModelScope.launch {
            favoriteApodRepository.getFavoriteApods()
                .collect { favoriteApods ->
                    currentApod?.let { apod ->
                        _uiState.value = ApodUiState.Success(
                            apod.copy(isFavorite = favoriteApods.any { favoriteApod ->
                                favoriteApod.date == apod.date
                            })
                        )
                    }
                }
        }
    }

    private fun addToFavorites(apod: Apod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            if (favoriteApodRepository.addFavoriteApod(apod.toFavoriteApod(isNew = true))) {
                _uiState.value = ApodUiState.Success(apod.copy(isFavorite = true))
            }
        }
    }

    private fun removeFromFavorites(apod: Apod) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            if (favoriteApodRepository.removeFavoriteApod(apod.toFavoriteApod(isNew = true))) {
                _uiState.value = ApodUiState.Success(apod.copy(isFavorite = false))
            }
        }
    }
}