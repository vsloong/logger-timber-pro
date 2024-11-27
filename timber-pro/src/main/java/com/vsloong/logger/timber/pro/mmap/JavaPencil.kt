package com.vsloong.logger.timber.pro.mmap

import java.io.File
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock

class JavaPencil(
    private val logDir: File,
    private val bufferSize: Int = 1024 * 1,          // 缓冲区大小 (测试1K)
    private val maxFileSize: Long = 1024 * 2,       // 单个日志文件的最大大小 (测试2K)
) {
    private var mMappedByteBuffer: MappedByteBuffer

    private val mLogFile = File(logDir, "log.txt")
    private val mLock = ReentrantLock()

    private var mCurrentPosition: Long = 0 // 当前写入位置
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    init {
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val randomAccessFile = RandomAccessFile(mLogFile, "rw")
        val fileChannel = randomAccessFile.channel

        // 定位到文件末尾
        mCurrentPosition = fileChannel.size()
        mMappedByteBuffer = fileChannel.map(
            FileChannel.MapMode.READ_WRITE,
            mCurrentPosition, bufferSize.toLong()
        )
    }


    fun log(priority: Int, tag: String?, message: String) {
        mLock.lock()
        try {
            val timestamp = mDateFormat.format(Date())
            val logEntry = "[$timestamp] level=$priority, tag=$tag, msg=$message\n"
            val logBytes = logEntry.toByteArray(StandardCharsets.UTF_8)

            // 检查文件大小（超出限制则归档）
            if (mCurrentPosition + logBytes.size > maxFileSize) {
                archiveLogFile()
            }

            // 检查缓冲区是否足够
            if (mMappedByteBuffer.remaining() < logBytes.size) {
                expandBuffer(logBytes.size)
            }
            mMappedByteBuffer.put(logBytes)
            mCurrentPosition += logBytes.size
        } finally {
            mLock.unlock()
        }
    }

    private fun expandBuffer(minSize: Int) {
        mLock.lock()
        try {
            // 刷新当前缓冲区到文件
            mMappedByteBuffer.force()

            // 扩展文件大小并重新映射
            val newBufferSize = bufferSize.coerceAtLeast(minSize)
            val randomAccessFile = RandomAccessFile(mLogFile, "rw")
            val fileChannel = randomAccessFile.channel
            fileChannel.truncate(mCurrentPosition + newBufferSize) // 扩展文件
            mMappedByteBuffer =
                fileChannel.map(
                    FileChannel.MapMode.READ_WRITE,
                    mCurrentPosition,
                    newBufferSize.toLong()
                )
        } finally {
            mLock.unlock()
        }
    }

    private fun archiveLogFile() {
        mLock.lock()
        try {
            // 刷新并关闭当前日志
            mMappedByteBuffer.force()

            // 归档日志文件
            val newLogFile = createNewLogFile()
            mLogFile.renameTo(newLogFile)

            // 创建新的日志文件并重新初始化
            val randomAccessFile = RandomAccessFile(mLogFile, "rw")
            val fileChannel = randomAccessFile.channel
            fileChannel.truncate(0)
            mCurrentPosition = 0
            mMappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_WRITE, mCurrentPosition, bufferSize.toLong()
            )
        } finally {
            mLock.unlock()
        }
    }

    private fun createNewLogFile(): File {
        val dateFormat = SimpleDateFormat("yyyy-MMdd-HHmm-ssSSS", Locale.getDefault())
        val fileName = "log-${dateFormat.format(Date())}.txt"
        return File(logDir, fileName)
    }

    fun close() {
        mLock.lock()
        try {
            mMappedByteBuffer.force()
        } finally {
            mLock.unlock()
        }
    }
}