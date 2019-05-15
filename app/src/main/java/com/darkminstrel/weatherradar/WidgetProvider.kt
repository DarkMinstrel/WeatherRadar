package com.darkminstrel.weatherradar

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import io.reactivex.android.schedulers.AndroidSchedulers

class WidgetProvider: AppWidgetProvider() {

    @SuppressLint("CheckResult")
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if(appWidgetIds.isNotEmpty()){
            DBG("Updating widget")
            Storage.read(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    {bitmap -> updateWidgets(context, appWidgetManager, appWidgetIds, bitmap)},
                    {error -> DBG(error)})
        }
    }

    private fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, bitmap: Bitmap){
        for(appWidgetId in appWidgetIds) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val views = RemoteViews(context.packageName, R.layout.widget_main)
            views.setOnClickPendingIntent(R.id.iv_radar, pendingIntent)
            views.setImageViewBitmap(R.id.iv_radar, bitmap)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}