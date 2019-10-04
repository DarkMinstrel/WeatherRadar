package com.darkminstrel.weatherradar.ui.act_main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.SyncService
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ActMainViewModel(private val context: Context, private val prefs: Prefs, private val usecaseSync: UsecaseSync): ViewModel() {

    private var disposable: Disposable? = null
    private val liveDataTitle = MutableLiveData<String>()
    private val liveDataBitmap = MutableLiveData<DataHolder<TimedBitmap>>()
    fun getLiveDataTitle() = this.liveDataTitle as LiveData<String>
    fun getLiveDataBitmap() = this.liveDataBitmap as LiveData<DataHolder<TimedBitmap>>

    init {
        SyncService.schedule(context, prefs.getUpdatePeriod(), prefs.wifiOnly, false)
        reload()
    }

    fun reload(){
        val radar = prefs.getRadar()
        liveDataTitle.value = String.format("%s %s", context.getString(R.string.app_name), radar.getCity(context))

        liveDataBitmap.value = null
        disposable?.dispose()
        disposable = usecaseSync.getSyncSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack -> liveDataBitmap.value = DataHolder.Success(pack)},
                {error -> liveDataBitmap.value = DataHolder.Error(error)})
    }


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
        disposable = null
    }

}