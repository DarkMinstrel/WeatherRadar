package com.darkminstrel.weatherradar

import com.darkminstrel.weatherradar.data.Radars

class Preferences {

    companion object {
        fun getUpdatePeriodMillis():Long{
            return Config.DEFAULT_UPDATE_PERIOD //TODO
        }

        fun getRadar():Radars{
            return Config.DEFAULT_RADAR //TODO
        }
    }

}