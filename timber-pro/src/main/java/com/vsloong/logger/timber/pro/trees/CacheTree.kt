package com.vsloong.logger.timber.pro.trees

import android.content.Context
import com.vsloong.logger.timber.pro.Timber
import com.vsloong.logger.timber.pro.Timber.Forest
import com.vsloong.logger.timber.pro.mmap.JavaPencil
import java.io.File

class CacheTree(context: Context) : ATree() {

    private val fqcnIgnore = listOf(
        Timber::class.java.name,
        Forest::class.java.name,
        ATree::class.java.name,
        CacheTree::class.java.name
    )

    override val tag: String?
        get() = super.tag ?: Throwable().stackTrace
            .first { it.className !in fqcnIgnore }
            .let(::createStackElementTag)

    private val pencil: JavaPencil = JavaPencil(
        logDir = File(context.applicationContext.cacheDir, "timber_log")
    )

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        pencil.log(priority = priority, tag = tag, message = message)
    }
}