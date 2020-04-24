package com.darkminstrel.weatherradar.ui.act_main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.weatherradar.Broadcaster
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.DBGE
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

class ActMainViewModel(private val context: Context, private val prefs: Prefs, private val usecaseSync: UsecaseSync, private val broadcaster: Broadcaster): ViewModel() {

    private var disposable: Disposable? = null
    private var disposableAnimation: Disposable? = null
    private val _liveDataTitle = MutableLiveData<String>()
    private val _liveDataBitmap = MutableLiveData<DataHolder<TimedBitmap>>()
    private val _liveDataSlideshow = MutableLiveData<List<TimedBitmap>>()
    val liveDataTitle = _liveDataTitle as LiveData<String>
    val liveDataBitmap = _liveDataBitmap as LiveData<DataHolder<TimedBitmap>>
    val liveDataSlideshow = _liveDataSlideshow as LiveData<List<TimedBitmap>>
    private var tsBitmap:Long? = null

    init {
        broadcaster.scheduleSyncJob()
        reload()
    }

    fun reload(){
        val radar = prefs.getRadarEnum()
        _liveDataTitle.value = radar.getCity(context)
        _liveDataBitmap.value = null
        _liveDataSlideshow.value = null

        disposable?.dispose()
        disposableAnimation?.dispose()
        disposable = usecaseSync.getSyncSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { loadSlideshow(it) }
            .subscribeBy(
                onSuccess = {bitmap ->
                    tsBitmap = bitmap.ts
                    _liveDataBitmap.value = DataHolder.Success(bitmap)
                },
                onError = {error -> _liveDataBitmap.value = DataHolder.Error(error)})
    }

    fun onActivityStarted(){
        tsBitmap?.let {
            val elapsedSeconds = System.currentTimeMillis()/1000 - it
            if(elapsedSeconds > Config.SLIDESHOW_INTERVAL_SEC){
                DBG("Bitmap is outdated for $elapsedSeconds seconds, reloading...")
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
                    _liveDataSlideshow.value = if(slideshow.size>1) slideshow else null
                },
                onError = {
                    DBGE("Slideshow", it)
                    _liveDataSlideshow.value = null
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