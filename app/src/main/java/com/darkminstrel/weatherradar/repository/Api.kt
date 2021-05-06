package com.darkminstrel.weatherradar.repository

import androidx.core.text.HtmlCompat
import com.darkminstrel.weatherradar.BitmapFactory
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.EmptyImageRadarException
import com.darkminstrel.weatherradar.NoTimestampRadarException
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.regex.Pattern


class Api(private val downloader: Downloader, private val bitmapFactory: BitmapFactory) {

    private fun findTimestamp(radar:String, html:String):Long? {
        val pattern = String.format("/%s/%s_\\d{10}.png", radar, radar)
        val m = Pattern.compile(pattern).matcher(html)
        if (m.find()) {
            val s = m.group(0)!!
            val m2 = Pattern.compile("\\d{10}").matcher(s)
            if(m2.find()) return m2.group(0)!!.toLong()
        }
        return null
    }

    private fun findProblem(html:String):String? {
        val tag = "<div style='margin:80px 20px;font-size:18px;'>"
        val start = html.indexOf(tag)
        if(start!=-1){
            val end = html.indexOf("</div>", start+tag.length)
            if(end!=-1){
                return HtmlCompat.fromHtml(html.substring(start+tag.length, end), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            }
        }
        return null
    }

    suspend fun getLatestTimestamp(radar:String): DataHolder<Long> = withContext(Dispatchers.IO){
        val url = String.format(Config.URL_PAGE, radar)
        val html:String
        try {
            html = downloader.download(url).string()
        }catch (e:Throwable){
            return@withContext DataHolder.Error(e)
        }
        val timestamp = findTimestamp(radar, html)
        if(timestamp!=null) return@withContext DataHolder.Success(timestamp)
        val problem = findProblem(html)
        if(problem!=null) return@withContext DataHolder.Error(NoTimestampRadarException(problem))
        return@withContext DataHolder.Error(NoTimestampRadarException(null))
    }

    suspend fun getImage(radar: String, ts:Long): TimedBitmap = withContext(Dispatchers.IO){
        val url = String.format(Config.URL_IMAGE, radar, radar, ts)
        val body = downloader.download(url)
        val bitmap = bitmapFactory.decodeBody(body) ?: throw EmptyImageRadarException()
        return@withContext TimedBitmap(ts,bitmapFactory.prepareBitmap(bitmap, ts),radar)
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    suspend fun getSlideshow(timedBitmap:TimedBitmap):List<TimedBitmap>{
        val resultList = (0..Config.SLIDESHOW_ITEMS_COUNT).asFlow()
            .flatMapMerge {
                val t = timedBitmap.ts - (it * Config.SLIDESHOW_INTERVAL_SEC)
                flow {
                    emit(getImage(timedBitmap.radar, t))
                }
            }.toList()
        return resultList.sortedBy {it.ts}
    }

}