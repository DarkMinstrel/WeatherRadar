package com.darkminstrel.weatherradar.rx

import android.content.Context
import com.darkminstrel.weatherradar.Preferences
import com.darkminstrel.weatherradar.ui.WidgetProvider
import com.darkminstrel.weatherradar.data.ContentPack
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun getSyncSingle(context: Context): Single<ContentPack>{
    val radar = Preferences.getRadar(context)
    val api = Api()
    return api.getLatestTimestamp(radar.code)
        .flatMap { ts -> api.getImage(radar.code, ts).map{ bitmap -> Pair(ts,bitmap) }}
        .observeOn(Schedulers.computation())
        .map { pair -> ContentPack(pair.first, pair.second) }
        .flatMap { pack -> Storage.write(context, pack.bitmap).andThen(Single.just(pack))  }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess{ WidgetProvider.updateAllWidgets(context) }
}


