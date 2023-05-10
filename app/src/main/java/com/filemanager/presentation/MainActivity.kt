package com.filemanager.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.filemanager.BuildConfig
import com.filemanager.R
import com.filemanager.core.navigation.AppNavigator
import com.filemanager.ui.theme.FileManagerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onResume() {
        super.onResume()
        if(SDK_INT >= 30) {
            viewModel.onChangePermissionStatus(Environment.isExternalStorageManager())
        } else {
            val isGranted = checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            viewModel.onChangePermissionStatus(isGranted)
        }
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
                    if(!state.permissionGranted) {
                        PermissionAlertDialog {
                            if(SDK_INT >= 30) {
                                requestPermissionHighApi()
                            } else {
                                requestPermission()
                            }
                        }
                    } else {
                        if(state.isLoading) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(Modifier.align(Alignment.Center))
                            }
                        } else {
                            AppNavigator(rememberNavController())
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermissionHighApi() {
        if(!Environment.isExternalStorageManager()) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            666
        )
    }
}

@Composable
private fun PermissionAlertDialog(
    requestPermission: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.permission_dialog_title)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.permission_dialog_text)
            )
        },
        onDismissRequest = {},
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = requestPermission
            ) {
                Text(
                    stringResource(R.string.permission_dialog_confirm_text)
                )
            }
        },
    )
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