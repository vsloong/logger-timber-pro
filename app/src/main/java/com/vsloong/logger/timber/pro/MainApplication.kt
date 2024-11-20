package com.vsloong.logger.timber.pro

import android.app.Application
import com.vsloong.logger.timber.pro.trees.DebugTree

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(DebugTree())
    }
}