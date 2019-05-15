package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.os.Looper
import android.util.SparseArray
import android.util.SparseIntArray

class Utils {
    companion object{
        fun cropBitmap(bitmap:Bitmap):Bitmap {
            assertWorkerThread()
            val minSize = Math.min(bitmap.width, bitmap.height)
            return Bitmap.createBitmap(bitmap, 1,1, minSize-1, minSize-2);
        }
    }
}

fun assertWorkerThread(){
    if(Thread.currentThread() === Looper.getMainLooper().thread) throw RuntimeException("assertWorkerThread() failed")
}
