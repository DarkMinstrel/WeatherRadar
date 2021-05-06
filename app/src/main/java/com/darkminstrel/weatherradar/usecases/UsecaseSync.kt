package com.darkminstrel.weatherradar.usecases

import com.darkminstrel.weatherradar.Broadcaster
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.repository.Api
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.repository.Storage

class UsecaseSync(private val api: Api, private val prefs: Prefs, private val storage: Storage, private val broadcaster: Broadcaster) {

    suspend fun sync(): DataHolder<TimedBitmap> {
        val radar = prefs.getRadarEnum()
        val latestTimestamp:DataHolder<Long> = api.getLatestTimestamp(radar.code)
        return when(latestTimestamp){
            is DataHolder.Error -> latestTimestamp
            is DataHolder.Success -> {
                val ts = latestTimestamp.value
                try{
                    val timedBitmap = api.getImage(radar.code, ts)
                    storage.write(timedBitmap.bitmap)
                    broadcaster.updateAllWidgets()
                    DataHolder.Success(timedBitmap)
                }catch (e:Throwable){
                    DataHolder.Error(e)
                }
            }
        }
    }

    suspend fun getSlideshow(timedBitmap: TimedBitmap) = api.getSlideshow(timedBitmap)

}

