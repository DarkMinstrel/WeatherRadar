package com.darkminstrel.weatherradar.usecases

import com.darkminstrel.weatherradar.Broadcaster
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Api
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.repository.Storage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class UsecaseSync(private val api: Api, private val prefs: Prefs, private val storage: Storage, private val broadcaster: Broadcaster) {

    fun getSyncSingle(): Single<TimedBitmap> {
        val radar = prefs.getRadarEnum()
        return api.getLatestTimestamp(radar.code)
            .flatMap { ts -> api.getImage(radar.code, ts) }
            .flatMap { timedBitmap ->
                storage.write(timedBitmap.bitmap).andThen(Single.just(timedBitmap))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { broadcaster.updateAllWidgets() }
    }

    fun getSlideshow(timedBitmap: TimedBitmap):Single<List<TimedBitmap>> {
        return api.getSlideshow(timedBitmap)
    }

}

