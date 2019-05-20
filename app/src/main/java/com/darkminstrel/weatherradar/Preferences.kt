package com.darkminstrel.weatherradar

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.darkminstrel.weatherradar.data.Periods
import com.darkminstrel.weatherradar.data.Radars

class Preferences {

    companion object {
        const val KEY_RADAR = "RADAR"
        const val KEY_PERIOD = "PERIOD"
        const val KEY_WIFI_ONLY = "WIFI_ONLY"

        private fun getPrefs(context: Context): SharedPreferences{
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun getUpdatePeriod(context: Context):Periods{
            return Periods.findByMillis(getPrefs(context).getString(KEY_PERIOD, Config.DEFAULT_UPDATE_PERIOD.toString())!!.toLong())
        }

        fun putUpdatePeriod(context: Context, period: Periods){
            getPrefs(context).edit().putString(KEY_PERIOD, period.millis.toString()).apply()
        }

        fun getRadar(context: Context):Radars{
            return Radars.findByCode(getPrefs(context).getString(KEY_RADAR, Config.DEFAULT_RADAR)!!)
        }

        fun putRadar(context: Context, radars: Radars){
            getPrefs(context).edit().putString(KEY_RADAR, radars.code).apply()
        }

        fun getWifiOnly(context: Context):Boolean{
            return getPrefs(context).getBoolean(KEY_WIFI_ONLY, Config.DEFAULT_WIFI_ONLY)
        }

        fun putWifiOnly(context: Context, newValue:Boolean){
            getPrefs(context).edit().putBoolean(KEY_WIFI_ONLY, newValue).apply()
        }
    }

}