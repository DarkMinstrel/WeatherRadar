package com.darkminstrel.weatherradar

import android.content.Context
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.darkminstrel.weatherradar.data.Periods
import com.darkminstrel.weatherradar.data.Radars

class FrgSettings : PreferenceFragmentCompat() {

    private lateinit var listRadars:ListPreference
    private lateinit var listPeriods:ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = context!!

        val screen = preferenceManager.createPreferenceScreen(context)

        listRadars = ListPreference(context)
        listRadars.key = Preferences.KEY_RADAR
        listRadars.title = getString(R.string.radar)
        listRadars.isIconSpaceReserved = false
        listRadars.entries = Radars.values().map {getString(it.cityId)}.toTypedArray()
        listRadars.entryValues = Radars.values().map { it.code }.toTypedArray()
        listRadars.setOnPreferenceChangeListener { preference, newValue ->
            val newRadar = Radars.findByCode(newValue as String)
            Preferences.putRadar(context, newRadar)
            refresh(context)
            false
        }
        screen.addPreference(listRadars)

        listPeriods = ListPreference(context)
        listPeriods.key = Preferences.KEY_PERIOD
        listPeriods.title = getString(R.string.update_period)
        listPeriods.isIconSpaceReserved = false
        listPeriods.entries = Periods.values().map {it.getString(context)}.toTypedArray()
        listPeriods.entryValues = Periods.values().map { it.millis.toString() }.toTypedArray()
        listPeriods.setOnPreferenceChangeListener { preference, newValue ->
            val newPeriod = Periods.findByMillis((newValue as String).toLong())
            Preferences.putUpdatePeriod(context, newPeriod)
            refresh(context)
            SyncService.schedule(context, true)
            false
        }
        screen.addPreference(listPeriods)

        refresh(context)

        preferenceScreen = screen
    }

    private fun refresh(context: Context){
        val radar = Preferences.getRadar(context)
        val period = Preferences.getUpdatePeriod(context)

        listRadars.summary = getString(radar.cityId)
        listRadars.setValueIndex(Radars.values().indexOf(radar))

        listPeriods.summary = period.getString(context)
        listPeriods.setValueIndex(Periods.values().indexOf(period))

    }
}