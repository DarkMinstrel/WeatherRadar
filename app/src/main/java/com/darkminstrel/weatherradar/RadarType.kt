package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import androidx.annotation.StringRes
import java.util.*

enum class RadarType(@StringRes val stringId:Int, val color:Int) {
    CLOUD(R.string.radar_1, 0xFFFFFF/**/),
    RAIN_SMALL(R.string.radar_2, 0x9BEA8F/*0x9BEB8F*/),
    RAIN_MEDIUM(R.string.radar_3, 0x58FF42/**/),
    RAIN_LARGE(R.string.radar_4, 0x47C278),
    CONV_CLOUD(R.string.radar_5, 0x9BE1FF),
    CONV_RAIN_SMALL(R.string.radar_6, 0x4793F9),
    CONV_RAIN_MEDIUM(R.string.radar_7, 0x0C59FF/**/),
    CONV_RAIN_LARGE(R.string.radar_8, 0x6153C1/**/),
    STORM_SMALL(R.string.radar_9, 0xFF8C9B/*0xFF93A3*/),
    STORM_MEDIUM(R.string.radar_10, 0xFF3F35),
    STORM_LARGE(R.string.radar_11, 0xC20511),
    HAIL_SMALL(R.string.radar_12, 0xFFEB0A),
    HAIL_MEDIUM(R.string.radar_13, 0xFF9811),
    HAIL_LARGE(R.string.radar_14, 0xA84C06),
    HURRICANE_SMALL(R.string.radar_15, 0xDDA8FF),
    HURRICANE_MEDIUM(R.string.radar_16, 0xE859FF),
    HURRICANE_LARGE(R.string.radar_17, 0xBE1CFF)
    ;
    companion object {
        private fun find(color: Int): RadarType?{
            val c = color and 0xFFFFFF
            for(type in values()) if(type.color==c) return type
            return null
        }
        fun collectColors(bitmap: Bitmap): Map<RadarType, Int> {
            val map = IdentityHashMap<RadarType, Int>()
            for(x in 0 until bitmap.width){
                for(y in 0 until bitmap.height){
                    val c = bitmap.getPixel(x,y)
                    val type = find(c)
                    type?.let {
                        if(type == HURRICANE_SMALL) DBG("x=$x y=$y")
                        val oldCount = map[it] ?:0
                        map.put(it, oldCount+1)
                    }
                }
            }
            return map
        }
    }
}