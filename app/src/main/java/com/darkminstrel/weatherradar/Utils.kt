package com.darkminstrel.weatherradar

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import androidx.annotation.DrawableRes
import java.io.PrintWriter
import java.io.StringWriter

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
    val name = Thread.currentThread().name
    if (BuildConfig.DEBUG_FEATURES && !name.startsWith("RxComputationThreadPool")) throw RuntimeException("assertComputationScheduler() failed, actual thread: $name")
}
fun assertIoScheduler() {
    val name = Thread.currentThread().name
    if (BuildConfig.DEBUG_FEATURES && !name.startsWith("RxCachedThreadScheduler")) throw RuntimeException("assertIoScheduler() failed, actual thread: $name")
}

fun TextView.setTopDrawable(@DrawableRes drawableId: Int) = this.setCompoundDrawablesWithIntrinsicBounds(0,drawableId,0,0)

fun DBG(s:Any?){
    if(BuildConfig.DEBUG_FEATURES) {
        Log.d("RADARDBG", s.toString())
    }
}
fun DBGE(title:String, throwable:Throwable){
    if(BuildConfig.DEBUG_FEATURES) {
        DBG("$title: ${throwable.message}")
        val writer = PrintWriter(StringWriter())
        throwable.printStackTrace(writer)
        DBG(writer.toString())
    }
}
