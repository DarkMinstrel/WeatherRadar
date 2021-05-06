package com.darkminstrel.weatherradar

import java.io.IOException

interface RadarException

class EmptyImageRadarException: IOException(), RadarException
class NoTimestampRadarException(val reason:String?):IOException(), RadarException