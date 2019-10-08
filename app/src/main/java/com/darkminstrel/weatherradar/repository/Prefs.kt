package com.darkminstrel.weatherradar.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.data.Radar
import com.darkminstrel.weatherradar.data.UpdatePeriod
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Prefs(context: Context){
    companion object{
        const val KEY_RADAR = "radar"
        const val KEY_UPDATE_PERIOD = "update_period"
        const val KEY_WIFI_ONLY = "wifi_only"
    }

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var radar:String by StringPref(KEY_RADAR, Config.DEFAULT_RADAR)
    var updatePeriod:Long by LongPref(KEY_UPDATE_PERIOD, Config.DEFAULT_UPDATE_PERIOD)
    var wifiOnly:Boolean by BoolPref(KEY_WIFI_ONLY, Config.DEFAULT_WIFI_ONLY)

    fun getUpdatePeriod() = UpdatePeriod.findByMillis(this.updatePeriod)
    fun getRadarEnum() = Radar.findByCode(this.radar)

    private inner class BoolPref(private val key:String, private val defaultValue: Boolean = false) : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = sharedPreferences.getBoolean(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = sharedPreferences.edit().putBoolean(key, value).apply()
    }
    private inner class IntPref(private val key:String, private val defaultValue: Int = 0) : ReadWriteProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.getInt(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) = sharedPreferences.edit().putInt(key, value).apply()
    }
    private inner class LongPref(private val key:String, private val defaultValue: Long = 0) : ReadWriteProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.getLong(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) = sharedPreferences.edit().putLong(key, value).apply()
    }
    private inner class NullableStringPref(private val key:String, private val defaultValue: String? = null) : ReadWriteProperty<Any?, String?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String? = sharedPreferences.getString(key, defaultValue)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) = sharedPreferences.edit().putString(key, value).apply()
    }
    private inner class StringPref(private val key:String, private val defaultValue: String) : ReadWriteProperty<Any?, String> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String = sharedPreferences.getString(key, defaultValue)?:defaultValue
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) = sharedPreferences.edit().putString(key, value).apply()
    }
}
