package com.vsloong.logger.timber.pro

import com.vsloong.logger.timber.pro.trees.ATree
import java.util.Collections
import java.util.Collections.unmodifiableList

/**
 * Logging for lazy people.
 * https://github.com/JakeWharton/timber
 */
class Timber private constructor() {
    init {
        throw AssertionError()
    }

    companion object Forest : ATree() {

        @Volatile
        private var treeArray = emptyArray<ATree>()

        // Both fields guarded by 'trees'.
        private val trees = ArrayList<ATree>()

        @get:[JvmStatic JvmName("treeCount")]
        val treeCount get() = treeArray.size

        /** Log a verbose message with optional format args. */
        @JvmStatic
        override fun v(message: () -> String?) {
            treeArray.forEach { it.v(message) }
        }

        /** Log a verbose exception and a message with optional format args. */
        @JvmStatic
        override fun v(t: Throwable?, message: (() -> String?)?) {
            treeArray.forEach { it.v(t, message) }
        }


        /** Log a debug message with optional format args. */
        @JvmStatic
        override fun d(message: () -> String?) {
            treeArray.forEach { it.d(message) }
        }

        /** Log a debug exception and a message with optional format args. */
        @JvmStatic
        override fun d(t: Throwable?, message: (() -> String?)?) {
            treeArray.forEach { it.d(t, message) }
        }

        /** Log an info message with optional format args. */
        @JvmStatic
        override fun i(message: () -> String?) {
            treeArray.forEach { it.i(message) }
        }

        /** Log an info exception and a message with optional format args. */
        @JvmStatic
        override fun i(t: Throwable?, message: (() -> String?)?) {
            treeArray.forEach { it.i(t, message) }
        }

        /** Log a warning message with optional format args. */
        @JvmStatic
        override fun w(message: () -> String?) {
            treeArray.forEach { it.w(message) }
        }

        /** Log a warning exception and a message with optional format args. */
        @JvmStatic
        override fun w(t: Throwable?, message: (() -> String?)?) {
            treeArray.forEach { it.w(t, message) }
        }

        /** Log an error message with optional format args. */
        @JvmStatic
        override fun e(message: () -> String?) {
            treeArray.forEach { it.e(message) }
        }

        /** Log an error exception and a message with optional format args. */
        @JvmStatic
        override fun e(t: Throwable?, message: (() -> String?)?) {
            treeArray.forEach { it.e(t, message) }
        }

        /** Log an assert message with optional format args. */
        @JvmStatic
        override fun wtf(message: () -> String?) {
            treeArray.forEach { it.wtf(message) }
        }

        /** Log an assert exception and a message with optional format args. */
        @JvmStatic
        override fun wtf(t: Throwable?, message: (() -> String?)?) {
            treeArray.forEach { it.wtf(t, message) }
        }

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            throw AssertionError() // Missing override for log method.
        }

        /**
         * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
         * instance rather than using static methods or to facilitate testing.
         */
        @Suppress(
            "NOTHING_TO_INLINE", // Kotlin users should reference `Tree.Forest` directly.
            "NON_FINAL_MEMBER_IN_OBJECT" // For japicmp check.
        )
        @JvmStatic
        open inline fun asTree(): ATree = this

        /** Set a one-time tag for use on the next logging call. */
        @JvmStatic
        fun tag(tag: String): ATree {
            for (tree in treeArray) {
                tree.explicitTag.set(tag)
            }
            return this
        }

        /** Add a new logging tree. */
        @JvmStatic
        fun plant(tree: ATree) {
            require(tree !== this) { "Cannot plant Timber into itself." }
            synchronized(trees) {
                trees.add(tree)
                treeArray = trees.toTypedArray()
            }
        }

        /** Adds new logging trees. */
        @JvmStatic
        fun plant(vararg trees: ATree) {
            for (tree in trees) {
                requireNotNull(tree) { "trees contained null" }
                require(tree !== this) { "Cannot plant Timber into itself." }
            }
            synchronized(Forest.trees) {
                Collections.addAll(Forest.trees, *trees)
                treeArray = Forest.trees.toTypedArray()
            }
        }

        /** Remove a planted tree. */
        @JvmStatic
        fun uproot(tree: ATree) {
            synchronized(trees) {
                require(trees.remove(tree)) { "Cannot uproot tree which is not planted: $tree" }
                treeArray = trees.toTypedArray()
            }
        }

        /** Remove all planted trees. */
        @JvmStatic
        fun uprootAll() {
            synchronized(trees) {
                trees.clear()
                treeArray = emptyArray()
            }
        }

        /** Return a copy of all planted [trees][ATree]. */
        @JvmStatic
        fun forest(): List<ATree> {
            synchronized(trees) {
                return unmodifiableList(trees.toList())
            }
        }

    }
}
