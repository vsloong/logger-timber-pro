package com.vsloong.logger.timber.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsloong.logger.timber.pro.trees.ATree
import com.vsloong.logger.timber.pro.trees.CacheTree
import com.vsloong.logger.timber.pro.ui.theme.LoggertimberproTheme

class MainActivity : ComponentActivity() {

    private val TAG = "TimberLogger"
    private val e = Throwable("sample error")

    private var cacheTree: ATree? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        cacheTree = CacheTree(this)

        setContent {
            LoggertimberproTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        AppButton(text = "Verbose") {
                            Timber.v { "Verbose log" }
//                            Timber.v { "${23 / 0}" }
//                            Timber.tag(TAG).v(e)
//                            Timber.tag(TAG).v(e) { "Verbose log" }
                        }
                        AppButton(text = "Debug") {
                            Timber.d { "Debug log" }
//                            Timber.d { "${23 / 0}" }
//                            Timber.tag(TAG).d(e)
//                            Timber.tag(TAG).d(e) { "Debug log" }
                        }
                        AppButton(text = "Info") {
                            Timber.i { "Info log" }
//                            Timber.i { "${23 / 0}" }
//                            Timber.tag(TAG).i(e)
//                            Timber.tag(TAG).i(e) { "Info log" }
                        }
                        AppButton(text = "Warn") {
                            Timber.w { "Warn log" }
//                            Timber.w { "${23 / 0}" }
//                            Timber.tag(TAG).w(e)
//                            Timber.tag(TAG).w(e) { "Warn log" }
                        }
                        AppButton(text = "Error") {
                            Timber.e { "Error log" }
//                            Timber.e { "${23 / 0}" }
//                            Timber.tag(TAG).e(e)
//                            Timber.tag(TAG).e(e) { "Error log" }
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        val checked = remember {
                            mutableStateOf(false)
                        }
                        Text(text = "Write to local file")
                        Switch(checked = checked.value,
                            onCheckedChange = { check ->
                                checked.value = check

                                cacheTree?.let {
                                    if (check) {
                                        Timber.plant(it)
                                    } else {
                                        Timber.uproot(it)
                                    }
                                }
                            })
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

    private val logMessage = """
Attribution timer fired
Path:      attribution
ClientSdk: android4.38.5
Parameters:
	android_uuid     c322ded225-d5a2-4df2-8sd2-27sd3f4sdc9a6b
	api_level        28
	app_token        ms2d3dd33n899nw8w
	app_version      1.0.0
	attribution_deeplink 1
	created_at       2024-11-22T09:47:59.139Z+0800
	device_name      SM-G9500
	device_type      phone
	environment      sandbox
	event_buffering_enabled 0
	foreground       1
	google_app_set_id c39d8ae7-4021-a3d2-7347-5992d2310d594ae
	gps_adid         3b95sd3296-52d7-434e-ax3f-c58xsw2ss3s2a96
	gps_adid_attempt 1
	gps_adid_src     service
	initiated_by     sdk
	needs_response_details 1
	offline_mode_enabled 0
	os_name          android
	os_version       9
	package_name     com.wechat
	send_in_background_enabled 0
	tracking_enabled 1
	ui_mode          1
    """.trimIndent()
}