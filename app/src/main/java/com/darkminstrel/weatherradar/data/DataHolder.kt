package com.darkminstrel.weatherradar.data

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

sealed class DataHolder<out T> {
    data class Success<out T>(val value: T) : DataHolder<T>()
    data class Error(val error: Throwable) : DataHolder<Nothing>()
}

fun <T> Single<T>.subscribeToLiveData(liveData: MutableLiveData<DataHolder<T>>): Disposable {
    return subscribe (
        {data -> liveData.value = DataHolder.Success(data)},
        {error -> liveData.value = DataHolder.Error(error)})
}
fun <T> Observable<T>.subscribeToLiveData(liveData: MutableLiveData<DataHolder<T>>): Disposable {
    return subscribe (
        {data -> liveData.value = DataHolder.Success(data)},
        {error -> liveData.value = DataHolder.Error(error)})
}