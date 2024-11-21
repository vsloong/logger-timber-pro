package com.vsloong.logger.timber.pro.mmap

class Pencil {

    companion object {
        init {
            System.loadLibrary("timber-mmap")
        }
    }

    external fun stringFromJNI(): String

    external fun writeLog(logPath: String, data: String)
}