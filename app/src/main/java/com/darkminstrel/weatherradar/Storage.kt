package com.darkminstrel.weatherradar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class Storage {
    companion object{
        private val FN = "radar.png"

        private fun writeImpl(context: Context, bitmap: Bitmap){
            assertWorkerThread()
            synchronized(FN){
                context.openFileOutput(FN, Context.MODE_PRIVATE).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
        }

        private fun readImpl(context:Context):Bitmap {
            assertWorkerThread()
            synchronized(FN) {
                context.openFileInput(FN).use {
                    return BitmapFactory.decodeStream(it)
                }
            }
        }

        fun write(context: Context, bitmap: Bitmap): Single<Bitmap>{
            return Single
                .fromCallable {
                    writeImpl(context, bitmap)
                    bitmap
                }
                .subscribeOn(Schedulers.io())
        }

        fun read(context:Context): Single<Bitmap>{
            return Single
                .fromCallable { readImpl(context) }
                .subscribeOn(Schedulers.io())
        }
    }
}