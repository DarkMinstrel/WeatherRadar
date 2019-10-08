package com.darkminstrel.weatherradar.data

import android.graphics.Bitmap

data class TimedBitmap(val ts:Long, val bitmap: Bitmap, val radar: String)