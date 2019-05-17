package com.darkminstrel.weatherradar

import com.darkminstrel.weatherradar.data.Radars

class Config {
    companion object {
        val DEFAULT_RADAR = Radars.KIEV.code
        const val DEFAULT_UPDATE_PERIOD = 0L
        const val DEFAULT_WIFI_ONLY = false
    }
}