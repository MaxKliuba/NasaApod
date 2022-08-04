package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.data.*
import com.android.maxclub.nasaapod.data.repository.ApodRepository
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.data.util.MediaType
import com.android.maxclub.nasaapod.data.util.toApod
import com.android.maxclub.nasaapod.data.util.toImageInfo
import com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod.FavoriteApodFragment.Companion.ARG_FAVORITE_APOD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteApodViewModel @Inject constructor(
    private val apodRepository: ApodRepository,
    private val favoriteApodRepository: FavoriteApodRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoriteApodUiState>(FavoriteApodUiState.Loading())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FavoriteApodUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var fetchJob: Job? = null

    private val favoriteApod: FavoriteApod = savedStateHandle[ARG_FAVORITE_APOD]!!
    val currentApod: Apod
        get() = (uiState.value as? FavoriteApodUiState.Success)?.apod
            ?: uiState.value.cachedApod
            ?: favoriteApod.toApod()

    init {
        fetchCurrentApod()
    }

    fun onEvent(event: FavoriteApodEvent) {

        when (event) {
            is FavoriteApodEvent.OnShowData -> {
                viewModelScope.launch {
                    if (favoriteApod.isNew) {
                        favoriteApodRepository.updateFavoriteApods(favoriteApod.copy(isNew = false))
                    }
                }
            }
            is FavoriteApodEvent.OnRefresh -> {
                fetchCurrentApod()
            }
            is FavoriteApodEvent.OnError -> {
                viewModelScope.launch {
                    _uiEvent.emit(FavoriteApodUiEvent.OnShowError(event.exception))
                }
            }
            is FavoriteApodEvent.OnImageClick -> {
                viewModelScope.launch {
                    currentApod.let { apod ->
                        if (apod.mediaType == MediaType.IMAGE) {
                            _uiEvent.emit(FavoriteApodUiEvent.OnImageOpen(apod.toImageInfo()))
                        } else {
                            _uiEvent.emit(FavoriteApodUiEvent.OnShowImageError)
                        }
                    }
                }
            }
            is FavoriteApodEvent.OnVideoClick -> {
                viewModelScope.launch {
                    currentApod.let { apod ->
                        if (apod.mediaType == MediaType.VIDEO) {
                            _uiEvent.emit(FavoriteApodUiEvent.OnVideoOpen(apod.url))
                        } else {
                            _uiEvent.emit(FavoriteApodUiEvent.OnShowVideoError)
                        }
                    }
                }
            }
            is FavoriteApodEvent.OnShareButtonClick -> {
                viewModelScope.launch {
                    _uiEvent.emit(FavoriteApodUiEvent.OnShare(currentApod))
                }
            }
            is FavoriteApodEvent.OnDelete -> {
                viewModelScope.launch {
                    if (favoriteApodRepository.removeFavoriteApod(favoriteApod)) {
                        _uiEvent.emit(FavoriteApodUiEvent.OnDelete(favoriteApod))
                    }
                }
            }
        }
    }

    private fun fetchCurrentApod() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            apodRepository.getApodByDate(currentApod.date)
                .onStart {
                    _uiState.value = FavoriteApodUiState.Loading(_uiState.value.cachedApod)
                }.catch { exception ->
                    _uiState.value = FavoriteApodUiState.Error(exception, _uiState.value.cachedApod)
                }.collect { apod ->
                    _uiState.value = FavoriteApodUiState.Success(apod)
                }
        }
    }
}