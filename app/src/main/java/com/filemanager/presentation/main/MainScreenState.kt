package com.filemanager.presentation.main

import com.filemanager.domain.model.FileModel

data class MainScreenState(
    val files: List<FileModel> = emptyList(),
    val isPermissionDialogVisible: Boolean = false,
    val isLoading: Boolean = true,
)