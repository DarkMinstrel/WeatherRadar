package com.darkminstrel.weatherradar.ui

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.repository.Storage
import com.darkminstrel.weatherradar.ui.act_main.ActMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class WidgetProvider: AppWidgetProvider(), KoinComponent {

    private val storage:Storage by inject()

    @SuppressLint("CheckResult")
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if(appWidgetIds.isNotEmpty()){
            DBG("Updating widget")
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap:Bitmap = try{
                     storage.read()
                }catch (e:Throwable){
                    DBG(e)
                    (ContextCompat.getDrawable(context, R.drawable.dino) as BitmapDrawable).bitmap
                }
                updateWidgets(context, appWidgetManager, appWidgetIds, bitmap)
            }
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

}