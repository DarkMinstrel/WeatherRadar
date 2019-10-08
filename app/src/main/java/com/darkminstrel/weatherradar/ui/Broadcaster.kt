package com.darkminstrel.weatherradar.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.darkminstrel.weatherradar.assertUiThread

class Broadcaster(private val context:Context) {

    fun updateAllWidgets(){
        assertUiThread()
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

}