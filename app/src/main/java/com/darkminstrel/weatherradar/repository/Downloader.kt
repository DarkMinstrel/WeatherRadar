package com.darkminstrel.weatherradar.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import ru.gildor.coroutines.okhttp.await
import java.io.IOException

class Downloader {
    suspend fun download(url:String): ResponseBody = withContext(Dispatchers.IO){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).await()
        val body = response.body
        if(response.isSuccessful && body!=null) {
            return@withContext body
        }else{
            throw IOException("Request failed, code=${response.code} body=${response.message}")
        }
    }

}