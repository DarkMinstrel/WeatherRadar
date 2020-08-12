package com.darkminstrel.weatherradar.ui.act_main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.data.DataHolder
import com.darkminstrel.weatherradar.ui.act_settings.ActSettings
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ActMain : AppCompatActivity(R.layout.act_main) {

    private var vh: ActMainViewHolder? = null
    private val vm: ActMainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vh = ActMainViewHolder(findViewById(android.R.id.content), vm)
        vm.liveDataTitle.observe(this, Observer{supportActionBar?.subtitle = it})
        vm.liveDataBitmap.observe(this, Observer {
            when(it){
                is DataHolder.Success -> vh?.setImage(it.value.bitmap)
                is DataHolder.Error -> vh?.setError(it.error)
                else -> vh?.setProgress()
            }
        })
        vm.liveDataSlideshow.observe(this, Observer { vh?.setSlideshow(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_settings){
            startActivityForResult(Intent(this, ActSettings::class.java), 1)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vm.reload()
    }

    override fun onStart() {
        super.onStart()
        vm.onActivityStarted()
    }

    override fun onDestroy() {
        vh = null
        super.onDestroy()
    }

}
