package com.darkminstrel.weatherradar.data

import androidx.lifecycle.MutableLiveData

sealed class DataHolder<out T> {
    data class Success<out T>(val value: T) : DataHolder<T>()
    data class Error(val error: Throwable) : DataHolder<Nothing>()
}
