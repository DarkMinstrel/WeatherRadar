package com.darkminstrel.weatherradar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.rx.sync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ActMain : AppCompatActivity() {

    private lateinit var view:ViewMain
    private var disposable:Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        SyncService.schedule(this)

        val radar = Preferences.getRadar()
        val title = String.format("%s %s", getString(R.string.app_name), getString(radar.cityId))
        setTitle(title)

        view = ViewMain(findViewById(android.R.id.content))
        view.setProgress()

        disposable = sync(this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack->view.setImage(pack.bitmap)},
                {error->view.setError(error)})
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_settings){
            startActivity(Intent(this, ActSettings::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
