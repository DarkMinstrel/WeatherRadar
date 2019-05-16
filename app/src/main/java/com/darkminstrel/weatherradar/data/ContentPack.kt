package com.darkminstrel.weatherradar.data

import android.graphics.*
import android.graphics.Typeface.create
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.assertWorkerThread
import java.text.SimpleDateFormat
import java.util.*


class ContentPack(ts:String, rawBitmap: Bitmap) {

    val bitmap:Bitmap
    val types:Map<RadarType, Int>
    val millis:Long

    init{
        assertWorkerThread()
        this.bitmap = cropBitmap(rawBitmap)
        this.types = RadarType.collectColors(this.bitmap)
        this.millis = ts.toLong()*1000
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
        drawTime(bitmap, time)
        DBG(types)
    }

    companion object {
        private fun cropBitmap(bitmap:Bitmap):Bitmap {
            assertWorkerThread()
            return Bitmap.createBitmap(bitmap, 16,1, 495-16, bitmap.height-2);
        }
        private fun drawTime(bitmap: Bitmap, time:String){
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = Color.BLACK
            paint.typeface = create(Typeface.DEFAULT, Typeface.BOLD);
            paint.textSize = 32f
            val w = paint.measureText(time)
            canvas.drawText(time, bitmap.width - w - 16f, 16f + paint.textSize, paint)
        }
    }

}