package com.darkminstrel.weatherradar.usecases

import android.content.Context
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Api
import com.darkminstrel.weatherradar.repository.Storage
import com.darkminstrel.weatherradar.ui.WidgetProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class UsecaseSync(private val context: Context, private val prefs: Prefs, private val api: Api) {

    fun getSyncSingle(): Single<TimedBitmap> {
        val radar = prefs.getRadar()
        return api.getLatestTimestamp(radar.code)
            .flatMap { ts -> api.getImage(radar.code, ts) }
            .flatMap { timedBitmap ->
                Storage.write(
                    context,
                    timedBitmap.bitmap
                ).andThen(Single.just(timedBitmap))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { WidgetProvider.updateAllWidgets(context) }
    }

}

