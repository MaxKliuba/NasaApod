package com.android.maxclub.nasaapod.viewmodel

import androidx.lifecycle.*
import com.android.maxclub.nasaapod.model.Apod
import com.android.maxclub.nasaapod.repository.ApodRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class ApodViewModel(private val apodRepository: ApodRepository) : ViewModel() {
    private val _apod = MutableLiveData<Apod>()
    val apod: LiveData<Apod> = Transformations.switchMap(_apod) {
        MutableLiveData(it)
    }

    fun fetchApod() {
        viewModelScope.launch {
            val response = apodRepository.getApod()
            if (response.isSuccessful) {
                _apod.postValue(response.body())
            }
        }
    }

    fun fetchApodByDate(date: Date) {
        viewModelScope.launch {
            val response = apodRepository.getApodByDate(date)
            if (response.isSuccessful) {
                _apod.postValue(response.body())
            }
        }
    }

    fun fetchRandomApod() {
        viewModelScope.launch {
            val response = apodRepository.getRandomApod()
            if (response.isSuccessful) {
                _apod.postValue(response.body()?.get(0))
            }
        }
    }
}

class ApodViewModelFactory(private val apodRepository: ApodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
            ApodViewModel(apodRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}