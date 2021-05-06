package com.darkminstrel.weatherradar.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.darkminstrel.weatherradar.assertWorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val FILENAME = "radar.png"

class Storage(private val context: Context) {

    suspend fun write(bitmap: Bitmap) = withContext(Dispatchers.IO){
        assertWorkerThread()
        synchronized(FILENAME){
            context.openFileOutput(FILENAME, Context.MODE_PRIVATE).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    suspend fun read():Bitmap = withContext(Dispatchers.IO){
        assertWorkerThread()
        synchronized(FILENAME) {
            context.openFileInput(FILENAME).use {
                return@withContext BitmapFactory.decodeStream(it)
            }
        }
    }

}