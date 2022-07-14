package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.ApodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ApodViewModel @Inject constructor(
    private val apodRepository: ApodRepository
) : ViewModel() {
    private val _apod = MutableLiveData<Apod>()
    val apod: LiveData<Apod> = Transformations.switchMap(_apod) {
        MutableLiveData(it)
    }
    private var fetchJob: Job? = null

    fun fetchApod() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val response = apodRepository.getApod()
            if (response.isSuccessful) {
                _apod.postValue(response.body())
            }
        }
    }

    fun fetchApodByDate(date: Date) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val response = apodRepository.getApodByDate(date)
            if (response.isSuccessful) {
                _apod.postValue(response.body())
            }
        }
    }

    fun fetchRandomApod() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val response = apodRepository.getRandomApod()
            if (response.isSuccessful) {
                _apod.postValue(response.body()?.get(0))
            }
        }
    }
}