package com.darkminstrel.weatherradar

import android.os.Looper
import android.os.SystemClock
import android.util.Log

class Stopwatch(){
    private val tsStarted = SystemClock.elapsedRealtime()
    fun debug(s:String){
        val elapsed = SystemClock.elapsedRealtime() - tsStarted
        DBG("$s lasted for $elapsed ms")
    }
}

fun assertWorkerThread(){
    if(Thread.currentThread() == Looper.getMainLooper().thread) throw RuntimeException("assertWorkerThread() failed")
}
fun assertUiThread(){
    if(Thread.currentThread() != Looper.getMainLooper().thread) throw RuntimeException("assertUiThread() failed")
}

fun DBG(s:Any?){
    Log.d("RADARDBG", s.toString())
}
