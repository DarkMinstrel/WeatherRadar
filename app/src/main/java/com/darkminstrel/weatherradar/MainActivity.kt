package com.darkminstrel.weatherradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darkminstrel.weatherradar.rx.sync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private lateinit var view:ViewMain
    private var disposable:Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SyncService.schedule(this)

        view = ViewMain(findViewById(android.R.id.content))
        view.setProgress()

        disposable = sync(this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack->view.setImage(pack.bitmap)},
                {error->view.setError(error)})
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
