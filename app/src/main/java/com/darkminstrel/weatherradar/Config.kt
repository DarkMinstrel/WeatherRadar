package com.darkminstrel.weatherradar

import com.darkminstrel.weatherradar.data.Radar
import java.util.concurrent.TimeUnit

object Config {
    val DEFAULT_RADAR = Radar.KIEV.code
    const val DEFAULT_UPDATE_PERIOD = 0L
    const val DEFAULT_WIFI_ONLY = false

    const val URL_PAGE = "http://www.meteoinfo.by/radar/?q=%s"
    const val URL_IMAGE = "http://www.meteoinfo.by/radar/%s/%s_%d.png"
    const val SLIDESHOW_ITEMS_COUNT = 10
    val SLIDESHOW_INTERVAL_SEC = TimeUnit.MINUTES.toSeconds(10L)
    val SLIDESHOW_ANIMATION_PERIOD_MS = TimeUnit.MILLISECONDS.toMillis(150L)
}