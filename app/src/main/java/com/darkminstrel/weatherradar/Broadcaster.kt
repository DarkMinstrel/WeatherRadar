package com.darkminstrel.weatherradar

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.ui.WidgetProvider

class Broadcaster(private val context:Context, private val prefs:Prefs) {

    fun updateAllWidgets(){
        assertUiThread()
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    fun scheduleSyncJob(){
        SyncJob.schedule(context, prefs.getUpdatePeriod(), prefs.wifiOnly, false)
    }

}