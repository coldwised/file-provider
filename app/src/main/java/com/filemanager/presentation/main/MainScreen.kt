package com.filemanager.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filemanager.R
import com.filemanager.domain.model.FileModel
import java.text.SimpleDateFormat

@Composable
internal fun MainScreen(
    directoryName: String = stringResource(id = R.string.main_topbar_title),
    path: String? = null,
    onFileClick: (FileModel) -> Unit,
    onBackClick: () -> Unit,
    onShareFileClick: (FileModel) -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onStart(path)
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MainScreen(
        directoryName = directoryName,
        path = path,
        onFileClick = onFileClick,
        onBackClick = onBackClick,
        files = state.files,
        onShareFileClick = onShareFileClick,
        isLoading = state.isLoading,
        isPermissionDialogVisible = state.isPermissionDialogVisible,
        onChangePermissionDialogVisibility = viewModel::onChangePermissionDialogVisibility,
    )
}

@Composable
private fun MainScreen(
    directoryName: String,
    path: String?,
    onFileClick: (FileModel) -> Unit,
    onBackClick: () -> Unit,
    onShareFileClick: (FileModel) -> Unit,
    files: List<FileModel>,
    isPermissionDialogVisible: Boolean,
    isLoading: Boolean,
    onChangePermissionDialogVisibility: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            MainTopBar(directoryName, onBackClick, path != null)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ProgressIndicator(modifier = Modifier.align(Alignment.Center), isLoading)
            FilesList(files, onFileClick, onShareFileClick)
            PermissionAlertDialog(isPermissionDialogVisible, onChangePermissionDialogVisibility)
        }
    }
}

@Composable
private fun ProgressIndicator(modifier: Modifier, isLoading: Boolean) {
    if(isLoading) {
        CircularProgressIndicator(modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(topBarTitle: String, onBackClick: () -> Unit, isBackIconVisible: Boolean) {
    TopAppBar(
        title = {
            Text(
                text = topBarTitle
            )
        },
        navigationIcon = {
            if(!isBackIconVisible) return@TopAppBar
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun FilesList(
    files: List<FileModel>,
    onFileClick: (FileModel) -> Unit,
    onShareFileClick: (FileModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(files) { file ->
            FileItem(
                file = file,
                onFileClick = onFileClick,
                onShareFileClick = onShareFileClick,
            )
        }
    }
}

@Composable
private fun PermissionAlertDialog(
    isVisible: Boolean,
    onChangePermissionDialogVisibility: (Boolean) -> Unit,
) {
    if(isVisible) {
        AlertDialog(
            onDismissRequest = { onChangePermissionDialogVisibility(false) },
            confirmButton = {
                TextButton(onClick = { onChangePermissionDialogVisibility(true) }) {
                    Text(
                        stringResource(R.string.permission_dialog_confirm_text)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onChangePermissionDialogVisibility(false) }) {
                    Text(
                        color = MaterialTheme.colorScheme.error,
                        text = stringResource(R.string.permission_dialog_cancel_text)
                    )
                }
            },
        )
    }
}

@Composable
private fun FileItem(
    file: FileModel,
    onFileClick: (FileModel) -> Unit,
    onShareFileClick: (FileModel) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
    val formattedDate = remember {
        dateFormatter.format(file.creationDate)
    }
    val name = remember {
        file.name
    }
    val size = remember {
        file.size.toString()
    }
    val isChanged = remember {
        file.isChanged
    }
    val fileType = file.type
    val icon = when(fileType) {
        "audio" -> painterResource(id = R.drawable.ic_audio_24)
        "image" -> painterResource(id = R.drawable.ic_image_24)
        "text" -> painterResource(id = R.drawable.ic_text_24)
        "folder" -> painterResource(id = R.drawable.ic_folder_24)
        else -> painterResource(id = R.drawable.ic_file_24)
    }
    ListItem(
        modifier = Modifier
            .clickable {
                onFileClick(file)
            },
        leadingContent = {
            Icon(painter = icon, contentDescription = null)
        },
        trailingContent = if(fileType == "folder") null else {
            {
                IconButton(onClick = { onShareFileClick(file) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
            }
        },
        headlineContent = {
            Text(
                text = name
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = size + " Б"
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = formattedDate
                    )
                }
                if(isChanged) {
                    Text(
                        text = stringResource(R.string.file_changed),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    )
}