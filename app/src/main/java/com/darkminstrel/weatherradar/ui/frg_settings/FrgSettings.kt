package com.darkminstrel.weatherradar.ui.frg_settings

import android.content.Context
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.SyncService
import com.darkminstrel.weatherradar.data.UpdatePeriod
import com.darkminstrel.weatherradar.data.Radar
import org.koin.android.ext.android.inject

class FrgSettings : PreferenceFragmentCompat() {

    private lateinit var listRadars:ListPreference
    private lateinit var listPeriods:ListPreference
    private lateinit var cbWifiOnly:CheckBoxPreference
    private val prefs: Prefs by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = requireContext()
        val screen = preferenceManager.createPreferenceScreen(context)

        listRadars = ListPreference(context)
        listRadars.key = Prefs.KEY_RADAR
        listRadars.title = getString(R.string.radar)
        listRadars.isIconSpaceReserved = false
        listRadars.entries = Radar.values().map {it.getCity(context)}.toTypedArray()
        listRadars.entryValues = Radar.values().map { it.code }.toTypedArray()
        listRadars.setOnPreferenceChangeListener { _, newValue ->
            prefs.radar = newValue as String
            refresh(context)
            false
        }
        screen.addPreference(listRadars)

        val categoryUpdate = PreferenceCategory(context)
        categoryUpdate.title = getString(R.string.background_updates)
        categoryUpdate.isIconSpaceReserved = false
        screen.addPreference(categoryUpdate)

        listPeriods = ListPreference(context)
        listPeriods.isPersistent = false
        listPeriods.key = Prefs.KEY_UPDATE_PERIOD
        listPeriods.title = getString(R.string.update_period)
        listPeriods.isIconSpaceReserved = false
        listPeriods.entries = UpdatePeriod.values().map {it.getString(context)}.toTypedArray()
        listPeriods.entryValues = UpdatePeriod.values().map { it.millis.toString() }.toTypedArray()
        listPeriods.setOnPreferenceChangeListener { _, newValue ->
            prefs.updatePeriod = (newValue as String).toLong()
            refresh(context)
            SyncService.schedule(context, prefs.getUpdatePeriod(), prefs.wifiOnly, true)
            false
        }
        categoryUpdate.addPreference(listPeriods)

        cbWifiOnly = CheckBoxPreference(context)
        cbWifiOnly.key = Prefs.KEY_WIFI_ONLY
        cbWifiOnly.title = getString(R.string.wifi_only)
        cbWifiOnly.isIconSpaceReserved = false
        cbWifiOnly.setOnPreferenceChangeListener { _, newValue ->
            prefs.wifiOnly = newValue as Boolean
            refresh(context)
            SyncService.schedule(context, prefs.getUpdatePeriod(), prefs.wifiOnly, true)
            false
        }
        categoryUpdate.addPreference(cbWifiOnly)

        refresh(context)

        preferenceScreen = screen
    }

    private fun refresh(context: Context){
        val radar = prefs.getRadar()
        val period = prefs.getUpdatePeriod()
        val wifiOnly = prefs.wifiOnly
        val updatesEnabled = (period!=UpdatePeriod.NONE)

        listRadars.summary = radar.getCity(context)
        listRadars.setValueIndex(Radar.values().indexOf(radar))

        listPeriods.summary = period.getString(context)
        listPeriods.setValueIndex(UpdatePeriod.values().indexOf(period))

        cbWifiOnly.isEnabled = updatesEnabled
        cbWifiOnly.isChecked = wifiOnly
    }
}