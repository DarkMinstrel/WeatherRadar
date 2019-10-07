package com.darkminstrel.weatherradar

interface RadarException

class EmptyImageRadarException:Exception(), RadarException
class NoTimestampRadarException:Exception(), RadarException