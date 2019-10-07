package com.darkminstrel.weatherradar.ui.act_settings

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.R

class ActSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        if(intent?.action == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE){
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24px)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, FrgSettings())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}