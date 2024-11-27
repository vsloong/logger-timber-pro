package com.vsloong.logger.timber.pro.trees

import android.util.Log
import com.vsloong.logger.timber.pro.Timber
import com.vsloong.logger.timber.pro.Timber.Forest
import kotlin.math.min

/** A [ATree] for debug builds. Automatically infers the tag from the calling class. */
open class DebugTree : ATree() {
    private val fqcnIgnore = listOf(
        Timber::class.java.name,
        Forest::class.java.name,
        ATree::class.java.name,
        DebugTree::class.java.name
    )

    override val tag: String?
        get() = super.tag ?: Throwable().stackTrace
            .first { it.className !in fqcnIgnore }
            .let(::createStackElementTag)

    /**
     * Break up `message` into maximum-length chunks (if needed) and send to either
     * [Log.println()][Log.println] or
     * [Log.wtf()][Log.wtf] for logging.
     *
     * {@inheritDoc}
     */
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (message.length < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                Log.wtf(tag, message)
            } else {
                Log.println(priority, tag, message)
            }
            return
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = min(newline, i + MAX_LOG_LENGTH)
                val part = message.substring(i, end)
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, part)
                } else {
                    Log.println(priority, tag, part)
                }
                i = end
            } while (i < newline)
            i++
        }
    }
}
