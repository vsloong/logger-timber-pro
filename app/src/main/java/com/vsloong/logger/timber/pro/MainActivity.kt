package com.vsloong.logger.timber.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsloong.logger.timber.pro.mmap.Pencil
import com.vsloong.logger.timber.pro.ui.theme.LoggertimberproTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private val TAG = "TimberLogger"
    private val e = Throwable("sample error")

    private val pencil = Pencil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val logFile = File(this.cacheDir, "timber-log.txt")

        setContent {
            LoggertimberproTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text("native sample = ${pencil.stringFromJNI()}")

                        AppButton(text = "Verbose") {
                            Timber.v { "Verbose log" }
                            Timber.v { "${23 / 0}" }
                            Timber.tag(TAG).v(e)
                            Timber.tag(TAG).v(e) { "Verbose log" }
                        }
                        AppButton(text = "Debug") {
                            Timber.d { "Debug log" }
                            Timber.d { "${23 / 0}" }
                            Timber.tag(TAG).d(e)
                            Timber.tag(TAG).d(e) { "Debug log" }
                        }
                        AppButton(text = "Info") {
                            Timber.i { "Info log" }
                            Timber.i { "${23 / 0}" }
                            Timber.tag(TAG).i(e)
                            Timber.tag(TAG).i(e) { "Info log" }
                        }
                        AppButton(text = "Warn") {
                            Timber.w { "Warn log" }
                            Timber.w { "${23 / 0}" }
                            Timber.tag(TAG).w(e)
                            Timber.tag(TAG).w(e) { "Warn log" }
                        }
                        AppButton(text = "Error") {
                            Timber.e { "Error log" }
                            Timber.e { "${23 / 0}" }
                            Timber.tag(TAG).e(e)
                            Timber.tag(TAG).e(e) { "Error log" }

                            pencil.writeLog(
                                logPath = logFile.absolutePath,
                                data = "this is test, time ${System.currentTimeMillis()}"
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        AppButton(text = "Write to file") {
                            pencil.writeLog(
                                logPath = logFile.absolutePath,
                                data = "write file test, time at ${System.currentTimeMillis()}"
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AppButton(text: String, onClick: () -> Unit = {}) {
        Button(onClick = onClick) {
            Text(text)
        }
    }
}