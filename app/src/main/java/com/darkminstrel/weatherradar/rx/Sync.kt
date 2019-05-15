package com.darkminstrel.weatherradar.rx

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.darkminstrel.weatherradar.*
import com.darkminstrel.weatherradar.data.RadarType
import com.darkminstrel.weatherradar.data.Radars
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun sync(context: Context): Single<Bitmap>{
    val radar = Radars.KIEV
    val api = Api()
    return api.getLatestTimestamp(radar.code)
        .flatMap { ts -> api.getImage(radar.code, ts) }
        .observeOn(Schedulers.computation())
        .map { bitmap -> Utils.cropBitmap(bitmap) }
        .flatMap { bitmap -> Storage.write(context, bitmap) }
        .doOnSuccess { bitmap ->
            val types = RadarType.collectColors(bitmap)
            DBG(types)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess{ updateWidgets(context) }
}

private fun updateWidgets(context: Context){
    assertUiThread()
    val intent = Intent(context, WidgetProvider::class.java)
    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    context.sendBroadcast(intent)
}