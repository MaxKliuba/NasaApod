package com.android.maxclub.nasaapod.data

import java.io.Serializable
import java.util.*

sealed class ApodDate(val date: Date?, val id: UUID) : Serializable {
    class Today(date: Date? = null, id: UUID = UUID.randomUUID()) : ApodDate(date, id)
    class From(date: Date, id: UUID = UUID.randomUUID()) : ApodDate(date, id)
    class Random(date: Date? = null, id: UUID = UUID.randomUUID()) : ApodDate(date, id)
}
