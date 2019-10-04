package com.darkminstrel.weatherradar.ui.frg_settings

import android.content.Context
import androidx.preference.*
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.data.Radar
import com.darkminstrel.weatherradar.data.UpdatePeriod
import com.darkminstrel.weatherradar.repository.Prefs

class FrgSettingsViewHolder(context: Context, preferenceManager:PreferenceManager, private val prefs:Prefs, private val onUpdateChanged:(updatePeriod:UpdatePeriod, wifiOnly:Boolean)->Unit) {
    val screen:PreferenceScreen = preferenceManager.createPreferenceScreen(context)
    private val listRadars: ListPreference = ListPreference(context)
    private val listPeriods: ListPreference = ListPreference(context)
    private val cbWifiOnly: CheckBoxPreference = CheckBoxPreference(context)

    init{

        listRadars.isPersistent = false
        listRadars.key = Prefs.KEY_RADAR
        listRadars.title = context.getString(R.string.radar)
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
        categoryUpdate.title = context.getString(R.string.background_updates)
        categoryUpdate.isIconSpaceReserved = false
        screen.addPreference(categoryUpdate)

        listPeriods.isPersistent = false
        listPeriods.key = Prefs.KEY_UPDATE_PERIOD
        listPeriods.title = context.getString(R.string.update_period)
        listPeriods.isIconSpaceReserved = false
        listPeriods.entries = UpdatePeriod.values().map {it.getString(context)}.toTypedArray()
        listPeriods.entryValues = UpdatePeriod.values().map { it.millis.toString() }.toTypedArray()
        listPeriods.setOnPreferenceChangeListener { _, newValue ->
            prefs.updatePeriod = (newValue as String).toLong()
            refresh(context)
            onUpdateChanged.invoke(prefs.getUpdatePeriod(), prefs.wifiOnly)
            false
        }
        categoryUpdate.addPreference(listPeriods)

        cbWifiOnly.isPersistent = false
        cbWifiOnly.key = Prefs.KEY_WIFI_ONLY
        cbWifiOnly.title = context.getString(R.string.wifi_only)
        cbWifiOnly.isIconSpaceReserved = false
        cbWifiOnly.setOnPreferenceChangeListener { _, newValue ->
            prefs.wifiOnly = newValue as Boolean
            refresh(context)
            onUpdateChanged.invoke(prefs.getUpdatePeriod(), prefs.wifiOnly)
            false
        }
        categoryUpdate.addPreference(cbWifiOnly)

        refresh(context)
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