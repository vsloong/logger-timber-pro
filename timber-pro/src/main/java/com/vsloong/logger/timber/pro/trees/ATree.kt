package com.vsloong.logger.timber.pro.trees

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

/** A facade for handling logging calls. Install instances via [`Timber.plant()`][.plant]. */
abstract class ATree {
    @get:JvmSynthetic // Hide from public API.
    internal val explicitTag = ThreadLocal<String>()

    @get:JvmSynthetic // Hide from public API.
    internal open val tag: String?
        get() {
            val tag = explicitTag.get()
            if (tag != null) {
                explicitTag.remove()
            }
            return tag
        }

    /** Log a verbose message. */
    open fun v(message: () -> String?) {
        prepareLog(Log.VERBOSE, null, message)
    }

    /** Log a verbose exception and a message. */
    open fun v(t: Throwable?, message: (() -> String?)? = null) {
        prepareLog(Log.VERBOSE, t, message)
    }

    /** Log a debug message. */
    open fun d(message: () -> String?) {
        prepareLog(Log.DEBUG, null, message)
    }

    /** Log a debug exception and a message. */
    open fun d(t: Throwable?, message: (() -> String?)? = null) {
        prepareLog(Log.DEBUG, t, message)
    }

    /** Log an info message. */
    open fun i(message: () -> String?) {
        prepareLog(Log.INFO, null, message)
    }

    /** Log an info exception and a message. */
    open fun i(t: Throwable?, message: (() -> String?)? = null) {
        prepareLog(Log.INFO, t, message)
    }

    /** Log a warning message. */
    open fun w(message: () -> String?) {
        prepareLog(Log.WARN, null, message)
    }

    /** Log a warning exception and a message. */
    open fun w(t: Throwable?, message: (() -> String?)? = null) {
        prepareLog(Log.WARN, t, message)
    }

    /** Log an error message. */
    open fun e(message: () -> String?) {
        prepareLog(Log.ERROR, null, message)
    }

    /** Log an error exception and a message. */
    open fun e(t: Throwable?, message: (() -> String?)? = null) {
        prepareLog(Log.ERROR, t, message)
    }

    /** Log an assert message. */
    open fun wtf(message: () -> String?) {
        prepareLog(Log.ASSERT, null, message)
    }

    /** Log an assert exception and a message. */
    open fun wtf(t: Throwable?, message: (() -> String?)? = null) {
        prepareLog(Log.ASSERT, t, message)
    }

    /** Return whether a message at `priority` should be logged. */
    @Deprecated("Use isLoggable(String, int)", ReplaceWith("this.isLoggable(null, priority)"))
    protected open fun isLoggable(priority: Int) = true

    /** Return whether a message at `priority` or `tag` should be logged. */
    protected open fun isLoggable(tag: String?, priority: Int) = isLoggable(priority)

    private fun prepareLog(priority: Int, t: Throwable?, message: (() -> String?)? = null) {
        // Consume tag even when message is not loggable so that next message is correctly tagged.
        val tag = tag
        if (!isLoggable(tag, priority)) {
            return
        }

        var realMessage = try {
            message?.invoke() ?: ""
        } catch (e: Throwable) {
            "wtf: message?.invoke() error: ${getStackTraceString(e)}"
        }

        if (t != null) {
            realMessage += "\n" + getStackTraceString(t)
        }

        log(priority, tag, realMessage, t)
    }


    private fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    /**
     * Write a log message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag Explicit or inferred tag. May be `null`.
     * @param message Formatted log message.
     * @param t Accompanying exceptions. May be `null`.
     */
    protected abstract fun log(priority: Int, tag: String?, message: String, t: Throwable?)
}