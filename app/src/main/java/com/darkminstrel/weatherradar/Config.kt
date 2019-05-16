package com.darkminstrel.weatherradar

import com.darkminstrel.weatherradar.data.Radars
import java.util.concurrent.TimeUnit

class Config {
    companion object {
        val DEFAULT_UPDATE_PERIOD = TimeUnit.MINUTES.toMillis(60)
        val DEFAULT_RADAR = Radars.KIEV
    }
}