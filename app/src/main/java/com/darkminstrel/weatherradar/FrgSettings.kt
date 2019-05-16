package com.darkminstrel.weatherradar

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.darkminstrel.weatherradar.data.Periods
import com.darkminstrel.weatherradar.data.Radars
import com.darkminstrel.weatherradar.rx.getSyncSingle

class FrgSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = context!!

        val screen = preferenceManager.createPreferenceScreen(context)

        val radar = Preferences.getRadar(context)
        val listRadars = ListPreference(context)
        listRadars.key = Preferences.KEY_RADAR
        listRadars.title = getString(R.string.radar)
        listRadars.summary = getString(radar.cityId)
        listRadars.isIconSpaceReserved = false
        listRadars.entries = Radars.values().map {getString(it.cityId)}.toTypedArray()
        listRadars.entryValues = Radars.values().map { it.code }.toTypedArray()
        listRadars.setValueIndex(Radars.values().indexOf(radar))
        listRadars.setOnPreferenceChangeListener { preference, newValue ->
            val newRadar = Radars.findByCode(newValue as String)
            listRadars.summary = getString(newRadar.cityId)
            listRadars.setValueIndex(Radars.values().indexOf(newRadar))
            Preferences.putRadar(context, newRadar)
            false
        }
        screen.addPreference(listRadars)

        val period = Preferences.getUpdatePeriod(context)
        val listPeriods = ListPreference(context)
        listPeriods.key = Preferences.KEY_PERIOD
        listPeriods.title = getString(R.string.update_period)
        listPeriods.summary = period.getString(context)
        listPeriods.isIconSpaceReserved = false
        listPeriods.entries = Periods.values().map {it.getString(context)}.toTypedArray()
        listPeriods.entryValues = Periods.values().map { it.millis.toString() }.toTypedArray()
        listPeriods.setValueIndex(Periods.values().indexOf(period))
        listPeriods.setOnPreferenceChangeListener { preference, newValue ->
            val newPeriod = Periods.findByMillis((newValue as String).toLong())
            listPeriods.summary = newPeriod.getString(context)
            listPeriods.setValueIndex(Periods.values().indexOf(newPeriod))
            Preferences.putUpdatePeriod(context, newPeriod)
            SyncService.schedule(context, true)
            false
        }
        screen.addPreference(listPeriods)

        preferenceScreen = screen
    }

}