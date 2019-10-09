package com.darkminstrel.weatherradar.ui.act_main

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.DBGE
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.ui.Broadcaster
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

class ActMainViewModel(private val context: Context, private val prefs: Prefs, private val usecaseSync: UsecaseSync, private val broadcaster: Broadcaster): ViewModel() {

    private var disposable: Disposable? = null
    private var disposableAnimation: Disposable? = null
    private val liveDataTitle = MutableLiveData<String>()
    private val liveDataBitmap = MutableLiveData<DataHolder<TimedBitmap>>()
    private val liveDataSlideshow = MutableLiveData<List<TimedBitmap>>()
    private var tsBitmap:Long? = null
    fun getLiveDataTitle() = this.liveDataTitle as LiveData<String>
    fun getLiveDataBitmap() = this.liveDataBitmap as LiveData<DataHolder<TimedBitmap>>
    fun getLiveDataSlideshow() = this.liveDataSlideshow as LiveData<List<TimedBitmap>>

    init {
        broadcaster.scheduleSyncJob()
        reload()
    }

    fun reload(){
        val radar = prefs.getRadarEnum()
        liveDataTitle.value = radar.getCity(context)
        liveDataBitmap.value = null
        liveDataSlideshow.value = null

        disposable?.dispose()
        disposableAnimation?.dispose()
        disposable = usecaseSync.getSyncSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { loadSlideshow(it) }
            .subscribeBy(
                onSuccess = {bitmap ->
                    tsBitmap = bitmap.ts
                    liveDataBitmap.value = DataHolder.Success(bitmap)
                },
                onError = {error -> liveDataBitmap.value = DataHolder.Error(error)})
    }

    fun onActivityStarted(){
        tsBitmap?.let {
            val elapsed = SystemClock.elapsedRealtime() - it
            if(elapsed > Config.SLIDESHOW_INTERVAL_SEC*1000L){
                DBG("Bitmap is outdated, reloading...")
                reload()
            }
        }
    }

    private fun loadSlideshow(timedBitmap: TimedBitmap){
        disposableAnimation?.dispose()
        disposableAnimation = usecaseSync.getSlideshow(timedBitmap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {slideshow ->
                    liveDataSlideshow.value = if(slideshow.size>1) slideshow else null
                },
                onError = {
                    DBGE("Slideshow", it)
                    liveDataSlideshow.value = null
                }
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