package com.tech.maxclub.nasaapod.domain.repository

import com.tech.maxclub.nasaapod.domain.model.Apod
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ApodRepository {

    fun getApodOfToday(date: Date? = null): Flow<Apod>

    fun getApodByDate(date: Date): Flow<Apod>

    fun getRandomApod(): Flow<Apod>
}