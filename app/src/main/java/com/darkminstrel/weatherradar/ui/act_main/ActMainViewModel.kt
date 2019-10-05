package com.darkminstrel.weatherradar.ui.act_main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.weatherradar.SyncService
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

class ActMainViewModel(private val context: Context, private val prefs: Prefs, private val usecaseSync: UsecaseSync): ViewModel() {

    private var disposable: Disposable? = null
    private var disposableAnimation: Disposable? = null
    private val liveDataTitle = MutableLiveData<String>()
    private val liveDataBitmap = MutableLiveData<DataHolder<TimedBitmap>>()
    private val liveDataSlideshow = MutableLiveData<List<TimedBitmap>>()
    fun getLiveDataTitle() = this.liveDataTitle as LiveData<String>
    fun getLiveDataBitmap() = this.liveDataBitmap as LiveData<DataHolder<TimedBitmap>>
    fun getLiveDataSlideshow() = this.liveDataSlideshow as LiveData<List<TimedBitmap>>

    init {
        SyncService.schedule(context, prefs.getUpdatePeriod(), prefs.wifiOnly, false)
        reload()
    }

    fun reload(){
        val radar = prefs.getRadar()
        liveDataTitle.value = radar.getCity(context)
        liveDataBitmap.value = null
        liveDataSlideshow.value = null

        disposable?.dispose()
        disposableAnimation?.dispose()
        disposable = usecaseSync.getSyncSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { loadSlideshow(it) }
            .subscribeBy(
                onSuccess = {bitmap -> liveDataBitmap.value = DataHolder.Success(bitmap)},
                onError = {error -> liveDataBitmap.value = DataHolder.Error(error)})
    }

    private fun loadSlideshow(timedBitmap: TimedBitmap){
        disposableAnimation?.dispose()
        disposableAnimation = usecaseSync.getSlideshow(timedBitmap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {slideshow -> liveDataSlideshow.value = slideshow},
                onError = {liveDataSlideshow.value = null}
            )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
        disposable = null
        disposableAnimation?.dispose()
        disposableAnimation = null
    }

}