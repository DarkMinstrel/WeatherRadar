package com.darkminstrel.weatherradar

import android.content.Context
import android.preference.PreferenceManager
import com.darkminstrel.weatherradar.data.Periods
import com.darkminstrel.weatherradar.data.Radars

class Preferences {

    companion object {

        const val KEY_RADAR = "RADAR"
        const val KEY_PERIOD = "PERIOD"
        const val KEY_WIFI_ONLY = "WIFI_ONLY"

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

        fun getWifiOnly(context: Context):Boolean{
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean(KEY_WIFI_ONLY, Config.DEFAULT_WIFI_ONLY)
        }

        fun putWifiOnly(context: Context, newValue:Boolean){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putBoolean(KEY_WIFI_ONLY, newValue).apply()
        }
    }

}