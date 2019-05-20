package com.darkminstrel.weatherradar.ui

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.RemoteViews
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.assertUiThread
import com.darkminstrel.weatherradar.rx.Storage
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
                    {error ->
                        run {
                            val bitmap = (context.getDrawable(R.drawable.dino) as BitmapDrawable).bitmap
                            updateWidgets(context, appWidgetManager, appWidgetIds, bitmap)
                            DBG(error)
                        }
                    })
        }
    }

    private fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, bitmap: Bitmap){
        for(appWidgetId in appWidgetIds) {
            val intent = Intent(context, ActMain::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val views = RemoteViews(context.packageName, R.layout.widget_main)
            views.setOnClickPendingIntent(R.id.iv_radar, pendingIntent)
            views.setImageViewBitmap(R.id.iv_radar, bitmap)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context){
            assertUiThread()
            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}