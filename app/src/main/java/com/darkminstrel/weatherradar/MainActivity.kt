package com.darkminstrel.weatherradar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

        val radar = "UKBB"
        val api = Api()
        disposable = api.getLatestTimestamp(radar)
            .flatMap { ts -> api.getImage(radar, ts) }
            .observeOn(Schedulers.computation())
            .map { bitmap -> Utils.cropBitmap(bitmap) }
            .doOnSuccess { bitmap -> DBG(RadarType.collectColors(bitmap)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {bitmap->
                    view.setImage(bitmap)
                },
                {
                    view.setError(it)
                })
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}

fun DBG(s:Any?){
    Log.d("RADARDBG", s.toString())
}