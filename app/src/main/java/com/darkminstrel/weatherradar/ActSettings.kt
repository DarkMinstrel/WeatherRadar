package com.darkminstrel.weatherradar

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_OK)   //as widget configuration activity

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, FrgSettings())
            .commit()
    }
}