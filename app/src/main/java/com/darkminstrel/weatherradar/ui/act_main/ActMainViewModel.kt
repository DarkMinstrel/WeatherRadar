package com.darkminstrel.weatherradar.ui.act_main

import android.app.Application
import androidx.lifecycle.*
import com.darkminstrel.weatherradar.Broadcaster
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.DBGE
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.NetworkNotifier
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActMainViewModel(private val app:Application, private val prefs: Prefs, private val usecaseSync: UsecaseSync, private val broadcaster: Broadcaster): AndroidViewModel(app) {

    private var disposable: Job? = null
    private var disposableAnimation: Job? = null
    private val _liveDataTitle = MutableLiveData<String>()
    private val _liveDataBitmap = MutableLiveData<DataHolder<TimedBitmap>>()
    private val _liveDataSlideshow = MutableLiveData<List<TimedBitmap>>()
    val liveDataTitle = _liveDataTitle as LiveData<String>
    val liveDataBitmap = _liveDataBitmap as LiveData<DataHolder<TimedBitmap>>
    val liveDataSlideshow = _liveDataSlideshow as LiveData<List<TimedBitmap>>
    private var tsBitmap:Long? = null
    private val networkNotifier = NetworkNotifier(app, onInternetAvailable = {
        if(_liveDataBitmap.value is DataHolder.Error) reload()
    })

    init {
        broadcaster.scheduleSyncJob()
        reload()
    }

    fun reload(){
        val radar = prefs.getRadarEnum()
        _liveDataTitle.value = radar.getCity(app)
        _liveDataBitmap.value = null
        _liveDataSlideshow.value = null

        disposable?.cancel()
        disposableAnimation?.cancel()
        disposable = viewModelScope.launch {
            val timedBitmap = usecaseSync.sync()
            when(timedBitmap){
                is DataHolder.Success -> {
                    val bitmap = timedBitmap.value
                    loadSlideshow(bitmap)
                    tsBitmap = bitmap.ts
                    _liveDataBitmap.value = DataHolder.Success(bitmap)
                }
                is DataHolder.Error -> {
                    _liveDataBitmap.value = DataHolder.Error(timedBitmap.error)
                }
            }
        }
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
        disposableAnimation?.cancel()
        disposableAnimation = viewModelScope.launch {
            try{
                val slideshow = usecaseSync.getSlideshow(timedBitmap)
                _liveDataSlideshow.value = if(slideshow.size>1) slideshow else null
            }catch (e:Throwable){
                DBGE("Slideshow", e)
                _liveDataSlideshow.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.cancel()
        disposable = null
        disposableAnimation?.cancel()
        disposableAnimation = null
        networkNotifier.unsubscribe()
    }

}