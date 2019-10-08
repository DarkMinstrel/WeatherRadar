package com.darkminstrel.weatherradar

import android.graphics.*
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*

class BitmapFactory {

    fun decodeBody(body:ResponseBody):Bitmap?{
        assertIoScheduler()
        return android.graphics.BitmapFactory.decodeStream(body.byteStream())
    }

    fun prepareBitmap(rawBitmap: Bitmap, ts:Long):Bitmap{
        assertComputationScheduler()
        val bitmap = cropBitmap(rawBitmap)
        drawTime(bitmap, ts*1000)
        return bitmap
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
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
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        paint.isAntiAlias = true

        paint.textSize = 32f
        val w = paint.measureText(time)
        canvas.drawText(time, bitmap.width - w - margin, margin + 32f, paint)

        paint.textSize = 16f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(date, bitmap.width - w/2 - margin, margin + 32f + 24f, paint)
    }

}