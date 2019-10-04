package com.darkminstrel.weatherradar.data

import android.content.Context
import com.darkminstrel.weatherradar.R
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

enum class UpdatePeriod(val millis:Long) {
        NONE(0),
        MIN30(TimeUnit.MINUTES.toMillis(30)),
        MIN60(TimeUnit.HOURS.toMillis(1)),
        HOUR1(TimeUnit.HOURS.toMillis(2)),
        HOUR2(TimeUnit.HOURS.toMillis(3)),
        HOUR3(TimeUnit.HOURS.toMillis(5))
        ;


    companion object {
        fun findByMillis(millis:Long):UpdatePeriod{
            for(period in values()) if(period.millis == millis) return period
            throw RuntimeException("Unknown period")
        }
    }

    fun getString(context: Context):String{
        if(millis==0L) return context.getString(R.string.dont_update)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis).toInt()
        val hours = minutes/60
        return if(hours>0){
            String.format(context.resources.getQuantityString(R.plurals.hours, hours, hours))
        }else{
            String.format(context.resources.getQuantityString(R.plurals.minutes, minutes, minutes))
        }
    }
}