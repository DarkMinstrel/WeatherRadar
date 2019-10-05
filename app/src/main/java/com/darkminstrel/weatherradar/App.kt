package com.darkminstrel.weatherradar

import android.app.Application
import com.darkminstrel.weatherradar.repository.Api
import com.darkminstrel.weatherradar.repository.Prefs
import com.darkminstrel.weatherradar.repository.Storage
import com.darkminstrel.weatherradar.ui.act_main.ActMainViewModel
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
        setupUndeliverableHandler()
    }

    private fun setupKoin(): KoinApplication {
        val appModule = module {
            single{ Storage(get()) }
            single{ Prefs(get()) }
            single{ Api() }
        }
        val usecaseModule = module {
            factory{ UsecaseSync(get(), get(), get(), get()) }
        }
        val vmModule = module {
            viewModel{ ActMainViewModel(get(), get(), get()) }
        }

        return startKoin {
            if(BuildConfig.DEBUG_FEATURES) androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, usecaseModule, vmModule))
        }
    }

    private fun setupUndeliverableHandler(){
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                DBGE(e)
            } else {
                Thread.currentThread().also { thread ->
                    thread.uncaughtExceptionHandler.uncaughtException(thread, e)
                }
            }
        }
    }

}