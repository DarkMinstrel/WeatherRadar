package com.darkminstrel.weatherradar.data

import android.content.Context
import androidx.annotation.StringRes
import com.darkminstrel.weatherradar.R

enum class Radar(val code:String, @StringRes private val cityId:Int) {
    KIEV("UKBB", R.string.city_kiev),
    ZP("UKDE", R.string.city_zp),
    MINSK("UMMN", R.string.city_minsk),
    BREST("UMBB", R.string.city_brest),
    HOMEL("UMGG", R.string.city_homel),
    ;
    companion object {
        fun findByCode(code:String):Radar{
            for(radar in values()) if(radar.code == code) return radar
            throw RuntimeException("Unknown radar code")
        }
    }
    fun getCity(context: Context):String{
        return context.getString(this.cityId)
    }
}