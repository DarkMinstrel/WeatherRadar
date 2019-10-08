package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import com.darkminstrel.weatherradar.data.Radar
import com.darkminstrel.weatherradar.repository.Api
import com.darkminstrel.weatherradar.repository.Downloader
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.repository.Storage
import com.darkminstrel.weatherradar.ui.Broadcaster
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import java.io.IOException


class UnitTestUsecaseSync {

    @Before
    fun setup(){
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { scheduler -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> Schedulers.trampoline() }
    }

    private val fakeBitmap = mock<Bitmap>()
    private val fakeResponseBody = mock<ResponseBody>{
        on{string()} doReturn "/UKBB/UKBB_1234567890.png"
    }

    private val bitmapFactory = mock<BitmapFactory>{
        on{ decodeBody(any()) } doReturn (fakeBitmap)
        on{ prepareBitmap(any(), anyLong()) } doReturn (fakeBitmap)

    }
    private val downloaderSuccess = mock<Downloader>{
        on{ download(anyString()) } doReturn(Single.just(fakeResponseBody))
    }
    private val downloaderNoNetwork = mock<Downloader>{
        on{ download(anyString()) } doReturn (Single.fromCallable<ResponseBody> { throw IOException() })
    }
    private val prefs = mock<Prefs> {
        on{ radar } doReturn (Radar.KIEV.code)
        on{ getRadarEnum() } doReturn (Radar.KIEV)
    }
    private val storage = mock<Storage> {
        on { write(any()) } doReturn (Completable.complete())
        on { read() } doReturn (Single.just(fakeBitmap))
    }
    private val broadcaster = mock<Broadcaster>()


    @Test
    fun testSyncSuccess(){
        val api = Api(downloaderSuccess, bitmapFactory)
        val useCase = UsecaseSync(api, prefs, storage, broadcaster)
        useCase.getSyncSingle().subscribe(
            { data ->
                Assert.assertNotNull(data.bitmap)
            },
            { error ->
                Assert.fail()
                error.printStackTrace()
            }
        )
    }

    @Test
    fun testSyncNoNetwork(){
        val api = Api(downloaderNoNetwork, bitmapFactory)
        val useCase = UsecaseSync(api, prefs, storage, broadcaster)
        useCase.getSyncSingle().subscribe(
            { data ->
                Assert.fail()
            },
            { error ->
                assert(error is IOException)
            }
        )
    }

}
