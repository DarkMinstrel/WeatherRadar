package com.darkminstrel.weatherradar.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import com.darkminstrel.weatherradar.assertIoScheduler
import com.darkminstrel.weatherradar.assertWorkerThread
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

private const val FILENAME = "radar.png"

class Storage(private val context: Context) {

    @WorkerThread
    private fun writeImpl(bitmap: Bitmap){
        assertIoScheduler()
        synchronized(FILENAME){
            context.openFileOutput(FILENAME, Context.MODE_PRIVATE).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    @WorkerThread
    private fun readImpl():Bitmap {
        assertIoScheduler()
        synchronized(FILENAME) {
            context.openFileInput(FILENAME).use {
                return BitmapFactory.decodeStream(it)
            }
        }
    }

    fun write(bitmap: Bitmap): Completable {
        return Completable
            .fromAction {
                writeImpl(bitmap)

            }
            .subscribeOn(Schedulers.io())
    }

    fun read(): Single<Bitmap>{
        return Single
            .fromCallable { readImpl() }
            .subscribeOn(Schedulers.io())
    }

}