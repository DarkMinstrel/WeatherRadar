package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.StringRes
import java.util.*

enum class RadarType(@StringRes val stringId:Int, val color:Int) {
    CLOUD(R.string.radar_1, 0xFFFFFF),
    RAIN_SMALL(R.string.radar_2, 0x9BEB8F),
    RAIN_MEDIUM(R.string.radar_3, 0x58FF42),
    RAIN_LARGE(R.string.radar_4, 0x47C278),
    CONV_CLOUD(R.string.radar_5, 0x9BE1FF),
    CONV_RAIN_SMALL(R.string.radar_6, 0x4793F9),
    CONV_RAIN_MEDIUM(R.string.radar_7, 0x0C59FF),
    CONV_RAIN_LARGE(R.string.radar_8, 0x6153C1),
    STORM_SMALL(R.string.radar_9, 0xFF93A3),
    STORM_MEDIUM(R.string.radar_10, 0xFF3F35),
    STORM_LARGE(R.string.radar_11, 0xC20511),
    HAIL_SMALL(R.string.radar_12, 0xFFEB0A),
    HAIL_MEDIUM(R.string.radar_13, 0xFF9811),
    HAIL_LARGE(R.string.radar_14, 0xA84C06),
    HURRICANE_SMALL(R.string.radar_15, 0xDDA8FF),
    HURRICANE_MEDIUM(R.string.radar_16, 0xE859FF),
    HURRICANE_LARGE(R.string.radar_17, 0xBE1CFF),
    ;
    companion object {
        private fun find(color: Int): RadarType?{
            for(type in values()) if(diff(type.color,color)<16) return type
            return null
        }
        private fun diff(c1:Int, c2:Int):Int{
            return Math.abs(Color.red(c1)-Color.red(c2))+Math.abs(Color.green(c1)-Color.green(c2))+Math.abs(Color.blue(c1)-Color.blue(c2))
        }
        fun collectColors(bitmap: Bitmap): Map<RadarType, Int> {
            val map = IdentityHashMap<RadarType, Int>()
            for(x in 0 until bitmap.width){
                for(y in 0 until bitmap.height){
                    val c = bitmap.getPixel(x,y)
                    val type = find(c)
                    type?.let {
                        val oldCount = map[it] ?:0
                        map.put(it, oldCount+1)
                    }
                }
            }
            val sorted = map.toList().sortedByDescending { (_, value) -> value}.toMap()
            return sorted
        }
    }
}