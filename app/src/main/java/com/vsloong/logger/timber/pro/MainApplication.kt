package com.vsloong.logger.timber.pro

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}