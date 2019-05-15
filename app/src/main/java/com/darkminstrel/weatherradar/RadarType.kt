package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import java.util.*

enum class RadarType(val color:Int) {
    CLOUD(0xFFFFFF/**/),
    RAIN_SMALL(0x9BEA8F/*0x9BEB8F*/),
    RAIN_MEDIUM(0x58FF42/**/),
    RAIN_LARGE(0x47C278),
    CONV_CLOUD(0x9BE1FF),
    CONV_RAIN_SMALL(0x4793F9),
    CONV_RAIN_MEDIUM(0x0C59FF/**/),
    CONV_RAIN_LARGE(0x6153C1/**/),
    STORM_SMALL(0xFF8C9B/*0xFF93A3*/),
    STORM_MEDIUM(0xFF3F35),
    STORM_LARGE(0xC20511),
    HAIL_SMALL(0xFFEB0A),
    HAIL_MEDIUM(0xFF9811),
    HAIL_LARGE(0xA84C06),
    HURRICANE_SMALL(0xDDA8FF),
    HURRICANE_MEDIUM(0xE859FF),
    HURRICANE_LARGE(0xBE1CFF)
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