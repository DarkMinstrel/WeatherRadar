package com.darkminstrel.weatherradar.repository

import com.darkminstrel.weatherradar.assertIoScheduler
import io.reactivex.Single
import okhttp3.*
import java.io.IOException

class Downloader {
    fun download(url:String): Single<ResponseBody> {
        return Single.create {
            assertIoScheduler()
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()
                    if(response.isSuccessful && body!=null) {
                        it.onSuccess(body)
                    }else{
                        it.onError(IOException("Request failed, code=${response.code()} body=${response.message()}"))
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    it.onError(e)
                }
            })
        }
    }

}