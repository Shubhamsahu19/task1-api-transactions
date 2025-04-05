package com.dictatenow.androidapp.utils

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.example.transactionsapiintegration.di.module.appModule
import com.example.transactionsapiintegration.model.ExceptionListener
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application(), ExceptionListener {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext((this@MyApplication))
            modules(appModule)
        }

       // setupExceptionHandler()
    }

    private fun setupExceptionHandler() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Extension.stopProgress()
        this.makeToast(throwable.message!!)
        this.makeToast(thread.name!!)
    }
}