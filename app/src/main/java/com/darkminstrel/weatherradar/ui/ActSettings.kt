package com.darkminstrel.weatherradar.ui

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.ui.frg_settings.FrgSettings

class ActSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_OK)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content,
                FrgSettings()
            )
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.getItemId() == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}