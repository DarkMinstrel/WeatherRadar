package com.darkminstrel.weatherradar.repository

import com.darkminstrel.weatherradar.BitmapFactory
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.EmptyImageRadarException
import com.darkminstrel.weatherradar.NoTimestampRadarException
import com.darkminstrel.weatherradar.data.TimedBitmap
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class Api(private val downloader: Downloader, private val bitmapFactory: BitmapFactory) {

    private fun findTimestamp(radar:String, html:String):Long {
        val pattern = String.format("/%s/%s_\\d{10}.png", radar, radar)
        val m = Pattern.compile(pattern).matcher(html)
        if (m.find()) {
            val s = m.group(0)
            val m2 = Pattern.compile("\\d{10}").matcher(s)
            if(m2.find()) return m2.group(0).toLong()
        }
        throw NoTimestampRadarException()
    }

    fun getLatestTimestamp(radar:String): Single<Long>{
        val url = String.format(Config.URL_PAGE, radar)
        return downloader.download(url)
            .subscribeOn(Schedulers.io())
            .map {body -> body.string()}
            .map {html -> findTimestamp(radar, html)}
    }

    fun getImage(radar: String, ts:Long): Single<TimedBitmap>{
        val url = String.format(Config.URL_IMAGE, radar, radar, ts)
        return downloader.download(url)
            .subscribeOn(Schedulers.io())
            .map {body -> bitmapFactory.decodeBody(body) ?: throw EmptyImageRadarException() }
            .observeOn(Schedulers.computation())
            .map {bitmap -> TimedBitmap(ts,bitmapFactory.prepareBitmap(bitmap, ts),radar)}
    }

    fun getSlideshow(timedBitmap:TimedBitmap):Single<List<TimedBitmap>>{
        val observables = ArrayList<Observable<TimedBitmap>>()
        (0..Config.SLIDESHOW_ITEMS_COUNT).forEach{
            val t = timedBitmap.ts - (it * Config.SLIDESHOW_INTERVAL_SEC)
            val observable = getImage(timedBitmap.radar, t)
                .toObservable()
                .onErrorResumeNext(Observable.empty())
            observables.add(observable)
        }
        return Observable.merge(observables)
            .toList()
            .map { list -> list.sortBy {it.ts}; list }
            .subscribeOn(Schedulers.io())
    }

}