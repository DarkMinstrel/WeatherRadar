package com.darkminstrel.weatherradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.data.RadarType
import com.darkminstrel.weatherradar.data.Radars
import com.darkminstrel.weatherradar.rx.Api
import com.darkminstrel.weatherradar.rx.Storage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var view:ViewMain
    private var disposable:Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = ViewMain(findViewById(android.R.id.content))
        view.setProgress()

        val radar = Radars.KIEV
        val api = Api()
        disposable = api.getLatestTimestamp(radar.code)
            .flatMap { ts -> api.getImage(radar.code, ts) }
            .observeOn(Schedulers.computation())
            .map { bitmap -> Utils.cropBitmap(bitmap) }
            .flatMap { bitmap -> Storage.write(this, bitmap) }
            .doOnSuccess { bitmap ->
                val types = RadarType.collectColors(bitmap)
                DBG(types)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess{ updateWidgets(this)}
            .subscribe(
                {bitmap->view.setImage(bitmap)},
                {error->view.setError(error)})
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
