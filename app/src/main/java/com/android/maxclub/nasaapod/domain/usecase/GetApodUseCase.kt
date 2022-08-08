package com.android.maxclub.nasaapod.domain.usecase

import com.android.maxclub.nasaapod.domain.model.Apod
import com.android.maxclub.nasaapod.domain.model.ApodDate
import com.android.maxclub.nasaapod.domain.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetApodUseCase @Inject constructor(
    private val apodRepository: ApodRepository
) {
    operator fun invoke(apodDate: ApodDate): Flow<Apod> =
        when (apodDate) {
            is ApodDate.Today -> apodRepository.getApodOfToday(apodDate.date)
            is ApodDate.From -> apodRepository.getApodByDate(apodDate.date)
            is ApodDate.Random -> apodRepository.getRandomApod()
        }
}