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
fun assertComputationScheduler() {
    if (BuildConfig.DEBUG_FEATURES && !Thread.currentThread().name.startsWith("RxComputationThreadPool")) throw RuntimeException("assertComputationScheduler() failed")
}

fun assertIoScheduler() {
    if (BuildConfig.DEBUG_FEATURES && !Thread.currentThread().name.startsWith("RxCachedThreadScheduler")) throw RuntimeException("assertIoScheduler() failed")
}

@Suppress("ConstantConditionIf", "FunctionName")
fun DBG(s:Any?){
    if(BuildConfig.DEBUG_FEATURES) {
        Log.d("RADARDBG", s.toString())
    }
}
fun DBGE(throwable:Throwable?){
    DBG(throwable?.message)
}
