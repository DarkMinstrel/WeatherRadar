package com.darkminstrel.weatherradar.ui.act_settings

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.darkminstrel.weatherradar.SyncService
import com.darkminstrel.weatherradar.data.UpdatePeriod
import com.darkminstrel.weatherradar.repository.Prefs
import org.koin.android.ext.android.inject

class FrgSettings : PreferenceFragmentCompat() {

    private val appctx: Context by inject()
    private val prefs: Prefs by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val vh = FrgSettingsViewHolder(requireContext(), preferenceManager, prefs, this::onUpdateChanged)
        preferenceScreen = vh.screen
    }

    private fun onUpdateChanged(updatePeriod:UpdatePeriod, wifiOnly:Boolean){
        SyncService.schedule(appctx, updatePeriod, wifiOnly, true)
    }
}