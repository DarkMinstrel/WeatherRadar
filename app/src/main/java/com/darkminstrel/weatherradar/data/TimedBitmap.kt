package com.darkminstrel.weatherradar.data

import android.graphics.*
import android.graphics.Typeface.create
import androidx.annotation.WorkerThread
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.assertComputationScheduler
import com.darkminstrel.weatherradar.assertWorkerThread
import java.text.SimpleDateFormat
import java.util.*

@WorkerThread
class TimedBitmap(ts:Long, rawBitmap: Bitmap, val radar: String) {
    val bitmap:Bitmap
    val ts:Long

    init{
        assertComputationScheduler()
        this.ts = ts
        this.bitmap = cropBitmap(rawBitmap)
        rawBitmap.recycle()
        drawTime(bitmap, ts*1000)
    }

    private fun cropBitmap(bitmap:Bitmap):Bitmap {
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