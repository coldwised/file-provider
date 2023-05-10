package com.filemanager.presentation

data class MainActivityState(
    val isLoading: Boolean = true,
    val permissionGranted: Boolean = false,
)