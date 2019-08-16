package com.darkminstrel.weatherradar.rx

import android.content.Context
import com.darkminstrel.weatherradar.Preferences
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.ui.WidgetProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun getSyncSingle(context: Context): Single<TimedBitmap>{
    val radar = Preferences.getRadar(context)
    val api = Api()
    return api.getLatestTimestamp(radar.code)
        .flatMap { ts -> api.getImage(radar.code, ts) }
        .flatMap { timedBitmap -> Storage.write(context, timedBitmap.bitmap).andThen(Single.just(timedBitmap))  }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess{ WidgetProvider.updateAllWidgets(context) }
}


