package com.filemanager.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filemanager.R
import com.filemanager.domain.model.FileModel
import com.filemanager.presentation.main.type.OrderType
import com.filemanager.presentation.main.type.SortType
import java.text.SimpleDateFormat
import kotlin.math.log10
import kotlin.math.pow

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
        listSortType = state.listSortType,
        listOrderType = state.listOrderType,
        sortTypeDropDownMenuVisible = state.sortTypeDropDownMenuVisible,
        orderTypeDropDownMenuVisible = state.orderTypeDropDownMenuVisible,
        onSortTypeChipClick = viewModel::onSortTypeChipClick,
        onOrderTypeChipClick = viewModel::onOrderTypeChipClick,
        onOrderTypeChange = viewModel::onListOrderTypeChange,
        onSortTypeChange = viewModel::onListSortTypeChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    directoryName: String,
    path: String?,
    onFileClick: (FileModel) -> Unit,
    onBackClick: () -> Unit,
    onShareFileClick: (FileModel) -> Unit,
    files: List<FileModel>,
    isLoading: Boolean,
    listSortType: SortType,
    listOrderType: OrderType,
    sortTypeDropDownMenuVisible: Boolean,
    orderTypeDropDownMenuVisible: Boolean,
    onOrderTypeChipClick: (Boolean) -> Unit,
    onSortTypeChange: (SortType) -> Unit,
    onOrderTypeChange: (OrderType) -> Unit,
    onSortTypeChipClick: (Boolean) -> Unit,
) {
    val enterAlwaysScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollBehavior = remember { enterAlwaysScrollBehavior }
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBar(
                topBarTitle = directoryName,
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick,
                sortTypeDropDownMenuVisible = sortTypeDropDownMenuVisible,
                orderTypeDropDownMenuVisible = orderTypeDropDownMenuVisible,
                onSortTypeChange = onSortTypeChange,
                onSortTypeChipClick = onSortTypeChipClick,
                onOrderTypeChipClick = onOrderTypeChipClick,
                onOrderTypeChange = onOrderTypeChange,
                listSortType = listSortType,
                listOrderType = listOrderType,
                isBackIconVisible = path != null
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ProgressIndicator(modifier = Modifier.align(Alignment.Center), isLoading)
            FilesList(files, onFileClick, onShareFileClick)
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
private fun MainTopBar(
    topBarTitle: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
    sortTypeDropDownMenuVisible: Boolean,
    orderTypeDropDownMenuVisible: Boolean,
    onSortTypeChange: (SortType) -> Unit,
    onOrderTypeChange: (OrderType) -> Unit,
    onSortTypeChipClick: (Boolean) -> Unit,
    onOrderTypeChipClick: (Boolean) -> Unit,
    listSortType: SortType,
    listOrderType: OrderType,
    isBackIconVisible: Boolean) {
    Column(
    ) {
        TopAppBar(
            scrollBehavior = scrollBehavior,
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
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            FilterChip(
                selected = true,
                onClick = { onSortTypeChipClick(true) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = listSortType.text
                    )
                    DropdownMenu(
                        expanded = sortTypeDropDownMenuVisible,
                        onDismissRequest = { onSortTypeChipClick(false) },
                        offset = DpOffset(0.dp, 0.dp)
                    ) {
                        SortType.values().forEach {
                            SortTypeDropDownItem(it, onSortTypeChange)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = true,
                onClick = { onOrderTypeChipClick(true) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = listOrderType.text
                    )
                    DropdownMenu(
                        expanded = orderTypeDropDownMenuVisible,
                        onDismissRequest = { onOrderTypeChipClick(false) },
                        offset = DpOffset(0.dp, 0.dp)
                    ) {
                        OrderType.values().forEach {
                            OrderTypeDropDownItem(it, onOrderTypeChange)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SortTypeDropDownItem(sortType: SortType, onClick: (SortType) -> Unit) {
    DropdownMenuItem(
        modifier = Modifier
            .height(34.dp)
            .width(180.dp),
        text = {
            Text(
                sortType.text
            )
        },
        onClick = { onClick(sortType) }
    )
}

@Composable
fun OrderTypeDropDownItem(sortType: OrderType, onClick: (OrderType) -> Unit) {
    DropdownMenuItem(
        modifier = Modifier
            .height(34.dp)
            .width(180.dp),
        text = {
            Text(
                sortType.text
            )
        },
        onClick = { onClick(sortType) }
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
        items(items = files) { file ->
            FileItem(
                file = file,
                onFileClick = onFileClick,
                onShareFileClick = onShareFileClick,
            )
        }
    }
}

@Composable
private fun FileItem(
    file: FileModel,
    onFileClick: (FileModel) -> Unit,
    onShareFileClick: (FileModel) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
    val formattedDate = dateFormatter.format(file.creationDate)
    val name = file.name
    val size = formatFileSize(file.size)
    val isChanged = file.isChanged
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
            Icon(painter = icon, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
        },
        trailingContent = {
            if(fileType == "folder") {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            } else {
                IconButton(
                    modifier = Modifier
                        .size(30.dp),
                    onClick = { onShareFileClick(file) }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
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
                        text = size
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

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return String.format("%.2f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}