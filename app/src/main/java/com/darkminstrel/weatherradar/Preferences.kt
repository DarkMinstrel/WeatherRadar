package com.darkminstrel.weatherradar

import android.content.Context
import android.preference.PreferenceManager
import com.darkminstrel.weatherradar.data.Periods
import com.darkminstrel.weatherradar.data.Radars

class Preferences {

    companion object {

        const val KEY_RADAR = "RADAR"
        const val KEY_PERIOD = "PERIOD"

        fun getUpdatePeriod(context: Context):Periods{
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return Periods.findByMillis(prefs.getString(KEY_PERIOD, Config.DEFAULT_UPDATE_PERIOD.toString())!!.toLong())
        }

        fun putUpdatePeriod(context: Context, period: Periods){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putString(KEY_PERIOD, period.millis.toString()).apply()
        }

        fun getRadar(context: Context):Radars{
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return Radars.findByCode(prefs.getString(KEY_RADAR, Config.DEFAULT_RADAR)!!)
        }

        fun putRadar(context: Context, radars: Radars){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putString(KEY_RADAR, radars.code).apply()
        }
    }

}