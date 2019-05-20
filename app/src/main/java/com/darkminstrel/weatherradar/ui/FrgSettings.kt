package com.darkminstrel.weatherradar.ui

import android.content.Context
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.darkminstrel.weatherradar.Preferences
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.SyncService
import com.darkminstrel.weatherradar.data.Periods
import com.darkminstrel.weatherradar.data.Radars

class FrgSettings : PreferenceFragmentCompat() {

    private lateinit var listRadars:ListPreference
    private lateinit var listPeriods:ListPreference
    private lateinit var cbWifiOnly:CheckBoxPreference

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

        val categoryUpdate = PreferenceCategory(context)
        categoryUpdate.title = getString(R.string.background_updates)
        categoryUpdate.isIconSpaceReserved = false
        screen.addPreference(categoryUpdate)

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
        categoryUpdate.addPreference(listPeriods)

        cbWifiOnly = CheckBoxPreference(context)
        cbWifiOnly.key = Preferences.KEY_WIFI_ONLY
        cbWifiOnly.title = getString(R.string.wifi_only)
        cbWifiOnly.isIconSpaceReserved = false
        cbWifiOnly.setOnPreferenceChangeListener { preference, newValue ->
            Preferences.putWifiOnly(context, newValue as Boolean)
            refresh(context)
            SyncService.schedule(context, true)
            false
        }
        categoryUpdate.addPreference(cbWifiOnly)

        refresh(context)

        preferenceScreen = screen
    }

    private fun refresh(context: Context){
        val radar = Preferences.getRadar(context)
        val period = Preferences.getUpdatePeriod(context)
        val wifiOnly = Preferences.getWifiOnly(context)
        val updatesEnabled = period!=Periods.NONE

        listRadars.summary = getString(radar.cityId)
        listRadars.setValueIndex(Radars.values().indexOf(radar))

        listPeriods.summary = period.getString(context)
        listPeriods.setValueIndex(Periods.values().indexOf(period))

        cbWifiOnly.isEnabled = updatesEnabled
        cbWifiOnly.isChecked = wifiOnly
    }
}