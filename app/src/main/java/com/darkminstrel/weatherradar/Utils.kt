package com.darkminstrel.weatherradar

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

@Suppress("ConstantConditionIf", "FunctionName")
fun DBG(s:Any?){
    if(BuildConfig.DEBUG_FEATURES) {
        Log.d("RADARDBG", s.toString())
    }
}
