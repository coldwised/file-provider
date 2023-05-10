package com.filemanager.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.filemanager.BuildConfig
import com.filemanager.core.navigation.AppNavigator
import com.filemanager.ui.theme.FileManagerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val state = viewModel.state.collectAsStateWithLifecycle().value
            TransparentSystemBars(isSystemInDarkTheme())
            FileManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        AppNavigator(rememberNavController())
                    }
                }
            }
        }
    }

    private fun checkPermission() {
        if(SDK_INT >= 30) {
            if(!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                startActivity(Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
            }
        }
    }
}

@Composable
fun TransparentSystemBars(isDarkTheme: Boolean) {
    val systemUiController = rememberSystemUiController()
    val transparentColor = Color.Transparent

    SideEffect {
        systemUiController.setSystemBarsColor(transparentColor)
        systemUiController.setNavigationBarColor(
            darkIcons = !isDarkTheme,
            color = transparentColor,
            navigationBarContrastEnforced = false,
        )
        systemUiController.setStatusBarColor(
            color = transparentColor,
            darkIcons = !isDarkTheme
        )
    }
}