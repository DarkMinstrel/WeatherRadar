package com.darkminstrel.weatherradar.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.*
import com.darkminstrel.weatherradar.events.EventBackgroundUpdate
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class ActMain : AppCompatActivity() {

    private var view: ViewMain? = null
    private var disposable:Disposable? = null
    private val prefs: Prefs by inject()
    private val usecaseSync:UsecaseSync by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        view = ViewMain(findViewById(android.R.id.content))
        SyncService.schedule(this, prefs.getUpdatePeriod(), prefs.wifiOnly, false)
        EventBus.getDefault().register(this)
        reload()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_settings){
            startActivityForResult(Intent(this, ActSettings::class.java), 1)
            return true
        }else if(item.itemId == R.id.menu_legend){
            view?.expandLegend()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        reload()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        disposable?.dispose()
        disposable = null
        view = null
        super.onDestroy()
    }

    @Suppress("unused", "RedundantVisibilityModifier")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEventBackgroundUpdate(event: EventBackgroundUpdate){
        DBG("onEventBackgroundUpdate")
        disposable?.dispose()
        view?.setImage(event.timedBitmap.bitmap)
    }

    private fun reload(){
        val radar = prefs.getRadar()
        val title = String.format("%s %s", getString(R.string.app_name), radar.getCity(this))
        setTitle(title)

        view?.setProgress()
        disposable?.dispose()
        disposable = usecaseSync.getSyncSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack->view?.setImage(pack.bitmap)},
                {error->view?.setError(error)})
    }

}
