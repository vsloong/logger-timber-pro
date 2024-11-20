package com.vsloong.logger.timber.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsloong.logger.timber.pro.ui.theme.LoggertimberproTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                            Timber.tag("TimberLogger").v("Verbose log")
                        }
                        AppButton(text = "Debug") {
                            Timber.tag("TimberLogger").d("Debug log")
                        }
                        AppButton(text = "Info") {
                            Timber.tag("TimberLogger").i("Info log")
                        }
                        AppButton(text = "Warn") {
                            Timber.tag("TimberLogger").w("Warn log")
                        }
                        AppButton(text = "Error") {
                            Timber.tag("TimberLogger").e("Error log")
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