package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.data.repository.ApodDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apodDateRepository: ApodDateRepository
) : ViewModel() {
    private val _apodDates = MutableStateFlow<List<ApodDate>>(emptyList())
    val apodDates: StateFlow<List<ApodDate>> = _apodDates
    private val _lastLoadedDate = MutableStateFlow<Date?>(null)
    val lastLoadedDate: StateFlow<Date?> = _lastLoadedDate
    var currentPosition: Int = 0

    init {
        viewModelScope.launch {
            launch {
                apodDateRepository.getApodDates()
                    .collect { apodDates ->
                        _apodDates.value = apodDates
                    }
            }

            launch {
                apodDateRepository.getLastLoadedDate()
                    .collect { date ->
                        _lastLoadedDate.value = date
                    }
            }
        }
    }

    fun addNewApodDate(newDate: ApodDate) =
        apodDateRepository.addNewApodDate(newDate)

    fun replaceAllWithNewApodDate(newDate: ApodDate) =
        apodDateRepository.replaceAllWithNewApodDate(newDate)
}