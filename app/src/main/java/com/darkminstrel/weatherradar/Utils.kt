package com.darkminstrel.weatherradar

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