package com.darkminstrel.weatherradar.ui.act_settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.darkminstrel.weatherradar.SyncJob
import com.darkminstrel.weatherradar.data.UpdatePeriod
import com.darkminstrel.weatherradar.repository.Prefs
import org.koin.android.ext.android.inject

class FrgSettings : PreferenceFragmentCompat() {

    private val appctx: Context by inject()
    private val prefs: Prefs by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val vh = FrgSettingsViewHolder(requireContext(), preferenceManager, prefs, this::onUpdateChanged)
        preferenceScreen = vh.screen
        updateActivityResult()
    }

    private fun onUpdateChanged(updatePeriod:UpdatePeriod, wifiOnly:Boolean){
        SyncJob.schedule(appctx, updatePeriod, wifiOnly, true)
        updateActivityResult()
    }

    private fun updateActivityResult(){
        activity?.setResult(if(prefs.getUpdatePeriod()!=UpdatePeriod.NONE) Activity.RESULT_OK else Activity.RESULT_CANCELED)
    }
}