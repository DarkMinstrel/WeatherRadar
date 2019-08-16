package com.darkminstrel.weatherradar.rx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.darkminstrel.weatherradar.assertWorkerThread
import com.darkminstrel.weatherradar.data.TimedBitmap
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern

private const val URL_PAGE = "http://www.meteoinfo.by/radar/?q=%s"
private const val URL_IMAGE = "http://www.meteoinfo.by/radar/%s/%s_%d.png"
private const val SLIDESHOW_ITEMS = 10
private const val SLIDESHOW_PERIOD = 600 //seconds


class Api {
    private fun download(url:String): Single<ResponseBody> {
        return Single.create {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object:Callback{
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()
                    if(response.isSuccessful && body!=null) {
                        it.onSuccess(body)
                    }else{
                        it.onError(IOException("Empty response, code=${response.code()} body=${response.message()}"))
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    it.onError(e)
                }
            })
        }
    }

    private fun findTimestamp(radar:String, html:String):Long {
        val pattern = String.format("/%s/%s_\\d{10}.png", radar, radar)
        val m = Pattern.compile(pattern).matcher(html)
        if (m.find()) {
            val s = m.group(0)
            val m2 = Pattern.compile("\\d{10}").matcher(s)
            if(m2.find()) return m2.group(0).toLong()
        }
        throw RuntimeException("Image URL not found")
    }

    fun getLatestTimestamp(radar:String): Single<Long>{
        val url = String.format(URL_PAGE, radar)
        return download(url)
            .map {body -> body.string()}
            .map {html -> findTimestamp(radar, html)}
            .subscribeOn(Schedulers.io())
    }

    fun getImage(radar: String, ts:Long): Single<TimedBitmap>{
        val url = String.format(URL_IMAGE, radar, radar, ts)
        return download(url)
            .map {body -> BitmapFactory.decodeStream(body.byteStream()) }
            .map {bitmap -> TimedBitmap(ts,bitmap)}
            .subscribeOn(Schedulers.io())
    }

    fun getSlideshow(radar: String, ts:Long):Single<List<TimedBitmap>>{
        val singles = ArrayList<Single<TimedBitmap>>()
        (0..SLIDESHOW_ITEMS).forEach{
            val t = ts - (it * SLIDESHOW_PERIOD)
            singles.add(getImage(radar, t))
        }
        return Single.merge(singles).toList()
            .subscribeOn(Schedulers.io())
    }

}