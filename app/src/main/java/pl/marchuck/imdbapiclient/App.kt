package pl.marchuck.imdbapiclient

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pl.marchuck.imdbapiclient.di.appModule
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ProductionTree())
        }
    }

    private class ProductionTree : Timber.Tree() {
        @SuppressLint("LogNotTimber")
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            when (priority) {
                //todo: send to Firebase / custom error reporting system
                Log.ERROR -> {
                    Log.e(tag, message, t)
                }
                Log.WARN -> {
                    Log.w(tag, message, t)
                }
            }
        }
    }
}