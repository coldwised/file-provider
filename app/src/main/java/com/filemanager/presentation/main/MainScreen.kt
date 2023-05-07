package com.filemanager.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.filemanager.R
import com.filemanager.domain.model.FileModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun MainScreen(
    directoryName: String = stringResource(id = R.string.main_topbar_title),
    path: String? = null,
    onFileClick: (FileModel) -> Unit,
    onBackClick: () -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onStart(path)
    }
    val state = viewModel.state.collectAsState().value
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
            FilesList(state.files, onFileClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(topBarTitle: String, onBackClick: () -> Unit, isBackIconVisible: Boolean) {
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
fun FilesList(files: List<FileModel>, onFileClick: (FileModel) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(files) { file ->
            FileItem(
                file = file,
                onFileClick = onFileClick,
            )
        }
    }
}

@Composable
fun FileItem(file: FileModel, onFileClick: (FileModel) -> Unit) {
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
    val icon = when(file.type) {
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
        headlineContent = {
            Text(
                text = name
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = size + " Б"
                )
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = formattedDate
                )
                if(isChanged) {
                    Text(
                        text = stringResource(R.string.file_changed),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    )
}