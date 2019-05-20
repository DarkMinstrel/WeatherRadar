package com.darkminstrel.weatherradar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.rx.getSyncSingle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ActMain : AppCompatActivity() {

    private lateinit var view:ViewMain
    private var disposable:Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        view = ViewMain(findViewById(android.R.id.content))
        SyncService.schedule(this, false)
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
        super.onDestroy()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBackgroundUpdate(event:EventBackgroundUpdate){
        DBG("onEventBackgroundUpdate")
        disposable?.dispose()
        view.setImage(event.pack.bitmap)
    }

    private fun reload(){
        val radar = Preferences.getRadar(this)
        val title = String.format("%s %s", getString(R.string.app_name), getString(radar.cityId))
        setTitle(title)

        view.setProgress()
        disposable?.dispose()
        disposable = getSyncSingle(this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack->view.setImage(pack.bitmap)},
                {error->view.setError(error)})
    }

}
