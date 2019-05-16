package com.darkminstrel.weatherradar.data

import android.graphics.*
import android.graphics.Typeface.create
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.assertWorkerThread
import java.text.DateFormat
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
        drawTime(bitmap, millis)
        DBG(types)
    }

    companion object {
        private fun cropBitmap(bitmap:Bitmap):Bitmap {
            assertWorkerThread()
            return Bitmap.createBitmap(bitmap, 16,1, 495-16, bitmap.height-2);
        }
        private fun drawTime(bitmap: Bitmap, millis:Long){
            val d = Date(millis)
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(d)
            val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(d)

            val margin = 12f
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.BLACK
            paint.typeface = create(Typeface.DEFAULT, Typeface.BOLD);
            paint.isAntiAlias = true

            paint.textSize = 32f
            val w = paint.measureText(time)
            canvas.drawText(time, bitmap.width - w - margin, margin + 32f, paint)

            paint.textSize = 16f
            val w2 = paint.measureText(date)
            canvas.drawText(date, bitmap.width - w2 - margin, margin + 32f + 24f, paint)
        }
    }

}