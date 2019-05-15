package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern

private const val URL_PAGE = "http://www.meteoinfo.by/radar/?q=%s"
private const val URL_IMAGE = "http://www.meteoinfo.by/radar/%s/%s_%s.png"

class Api {
    fun download(url:String): Single<ResponseBody> {
        return Single.create {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object:Callback{
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()
                    if(response.isSuccessful && body!=null) {
                        it.onSuccess(body)
                    }else{
                        it.onError(IOException("Empty response"))
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    it.onError(e)
                }
            })
        }
    }

    fun getLatestTimestamp(radar:String): Single<String>{
        val url = String.format(URL_PAGE, radar)
        return download(url)
            .map {body -> body.string()}
            .map {html -> findTimestamp(radar, html)}
            .subscribeOn(Schedulers.io())
    }

    fun getImage(radar: String, ts:String): Single<Bitmap>{
        val url = String.format(URL_IMAGE, radar, radar, ts)
        return download(url)
            .map {body -> BitmapFactory.decodeStream(body.byteStream()); }
            .subscribeOn(Schedulers.io())
    }

    private fun findTimestamp(radar:String, html:String):String {
        assertWorkerThread()
        val pattern = String.format("/%s/%s_\\d{10}.png", radar, radar)
        val m = Pattern.compile(pattern).matcher(html)
        if (m.find()) {
            val s = m.group(0)
            val m2 = Pattern.compile("\\d{10}").matcher(s)
            if(m2.find()) return m2.group(0)
        }
        throw RuntimeException("Image URL not found")
    }
}