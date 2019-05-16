package com.darkminstrel.weatherradar

import com.darkminstrel.weatherradar.data.Radars
import java.util.concurrent.TimeUnit

class Config {
    companion object {
        val DEFAULT_UPDATE_PERIOD = 0L
        val DEFAULT_RADAR = Radars.KIEV.code
    }
}