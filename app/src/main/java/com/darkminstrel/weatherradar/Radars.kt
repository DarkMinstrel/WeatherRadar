package com.darkminstrel.weatherradar

import androidx.annotation.StringRes

enum class Radars(val code:String, @StringRes val cityId:Int) {
    KIEV("UKBB", R.string.city_kiev),
    MINSK("UMMN", R.string.city_minsk),
    BREST("UMBB", R.string.city_brest),
    HOMEL("UMGG", R.string.city_homel),
    ;

}