package com.filemanager.presentation.main

import com.filemanager.domain.model.FileModel
import com.filemanager.presentation.main.type.OrderType
import com.filemanager.presentation.main.type.SortType

data class MainScreenState(
    val files: List<FileModel> = emptyList(),
    val isLoading: Boolean = true,
    val listOrderType: OrderType = OrderType.ByAscending,
    val sortTypeDropDownMenuVisible: Boolean = false,
    val orderTypeDropDownMenuVisible: Boolean = false,
    val listSortType: SortType = SortType.ByName,
)