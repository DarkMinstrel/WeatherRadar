package com.darkminstrel.weatherradar

import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*

class BitmapFactory {

    suspend fun decodeBody(body:ResponseBody):Bitmap? = withContext(Dispatchers.IO){
        //TODO switch to ImageDecoder.decodeBitmap
        return@withContext android.graphics.BitmapFactory.decodeStream(body.byteStream())
    }

    suspend fun prepareBitmap(rawBitmap: Bitmap, ts:Long):Bitmap = withContext(Dispatchers.IO){
        val bitmap = cropBitmap(rawBitmap)
        drawTime(bitmap, ts*1000)
        return@withContext bitmap
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(bitmap, 16,1, 495-16, bitmap.height-2);
    }

    private fun drawTime(bitmap: Bitmap, millis:Long){
        val d = Date(millis)
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(d)
        val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(d)

        val margin = 12f
        val marginHalf = margin/2
        val canvas = Canvas(bitmap)
        val paintTime = Paint().apply {
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
            isAntiAlias = true
            color = Color.BLACK
            textSize = 32f
        }
        val paintDate = Paint().apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
            isAntiAlias = true
            color = Color.BLACK
            textSize = 16f
        }
        val paintBg = Paint().apply {
            color = 0xA0CCCCCC.toInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val paintStroke = Paint().apply {
            color = 0xA0000000.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        val widthTime = paintTime.measureText(time)
        canvas.drawRoundRect(bitmap.width-widthTime-margin-marginHalf, marginHalf, bitmap.width.toFloat()-marginHalf, paintTime.textSize+paintDate.textSize+2*margin+marginHalf, marginHalf, marginHalf, paintBg)
        canvas.drawRoundRect(bitmap.width-widthTime-margin-marginHalf, marginHalf, bitmap.width.toFloat()-marginHalf, paintTime.textSize+paintDate.textSize+2*margin+marginHalf, marginHalf, marginHalf, paintStroke)
        canvas.drawText(time, bitmap.width - widthTime - margin, margin + 32f, paintTime)
        canvas.drawText(date, bitmap.width - widthTime/2 - margin, margin + 32f + 24f, paintDate)
    }

}