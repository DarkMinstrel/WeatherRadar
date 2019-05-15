package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.StringRes
import java.util.*

enum class RadarType(@StringRes val stringId:Int, val color:Int) {
    CLOUD(R.string.radar_1, 0xFFFFFFFF.toInt()),
    RAIN_SMALL(R.string.radar_2, 0xFF9BEB8F.toInt()),
    RAIN_MEDIUM(R.string.radar_3, 0xFF58FF42.toInt()),
    RAIN_LARGE(R.string.radar_4, 0xFF47C278.toInt()),
    CONV_CLOUD(R.string.radar_5, 0xFF9BE1FF.toInt()),
    CONV_RAIN_SMALL(R.string.radar_6, 0xFF4793F9.toInt()),
    CONV_RAIN_MEDIUM(R.string.radar_7, 0xFF0C59FF.toInt()),
    CONV_RAIN_LARGE(R.string.radar_8, 0xFF6153C1.toInt()),
    STORM_SMALL(R.string.radar_9, 0xFFFF93A3.toInt()),
    STORM_MEDIUM(R.string.radar_10, 0xFFFF3F35.toInt()),
    STORM_LARGE(R.string.radar_11, 0xFFC20511.toInt()),
    HAIL_SMALL(R.string.radar_12, 0xFFFFEB0A.toInt()),
    HAIL_MEDIUM(R.string.radar_13, 0xFFFF9811.toInt()),
    HAIL_LARGE(R.string.radar_14, 0xFFA84C06.toInt()),
    HURRICANE_SMALL(R.string.radar_15, 0xFFDDA8FF.toInt()),
    HURRICANE_MEDIUM(R.string.radar_16, 0xFFE859FF.toInt()),
    HURRICANE_LARGE(R.string.radar_17, 0xFFBE1CFF.toInt()),
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
            val stopwatch = Stopwatch()
            val map = IdentityHashMap<RadarType, Int>()
            for(x in 0 until bitmap.width){
                for(y in 0 until bitmap.height){
                    val c = bitmap.getPixel(x,y)
                    if(c == 0xFFCCCCCC.toInt()) continue
                    val type = find(c)
                    type?.let {
                        val oldCount = map[it] ?:0
                        map.put(it, oldCount+1)
                    }
                }
            }
            stopwatch.debug("Collecting colors")
            return map.toList().sortedByDescending { (_, value) -> value}.toMap()
        }
    }
}