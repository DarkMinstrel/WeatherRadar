package com.darkminstrel.weatherradar

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Looper
import android.os.SystemClock
import android.util.Log


class Utils {
    companion object{
        fun cropBitmap(bitmap:Bitmap):Bitmap {
            assertWorkerThread()
            val minSize = Math.min(bitmap.width, bitmap.height)
            return Bitmap.createBitmap(bitmap, 1,1, minSize-1, minSize-2);
        }
    }
}

class Stopwatch(){
    private val tsStarted = SystemClock.elapsedRealtime()
    fun debug(s:String){
        val elapsed = SystemClock.elapsedRealtime() - tsStarted
        DBG("$s lasted for $elapsed ms")
    }
}

fun assertWorkerThread(){
    if(Thread.currentThread() === Looper.getMainLooper().thread) throw RuntimeException("assertWorkerThread() failed")
}


fun DBG(s:Any?){
    Log.d("RADARDBG", s.toString())
}

fun updateWidgets(context: Context){
    val intent = Intent(context, WidgetProvider::class.java)
    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    context.sendBroadcast(intent)
}