package com.filemanager.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filemanager.domain.usecase.GetFilesUseCase
import com.filemanager.presentation.main.type.OrderType
import com.filemanager.presentation.main.type.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getFilesUseCase: GetFilesUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    fun onStart(path: String?) {
        val state = _state
        if(!state.value.isLoading)
            return
        viewModelScope.launch {
            getFilesUseCase(path).collect { files ->
                _state.update {
                    it.copy(
                        files = files,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onSortTypeChipClick(dropDownMenuVisible: Boolean) {
        _state.update {
            it.copy(
                sortTypeDropDownMenuVisible = dropDownMenuVisible
            )
        }
    }

    fun onListSortTypeChange(sortType: SortType) {
        viewModelScope.launch {
            _state.update {
                val files = it.files
                it.copy(
                    files = when(sortType) {
                        SortType.ByName -> {
                            if(it.listOrderType == OrderType.ByDescending) {
                                files.sortedByDescending { fileModel ->
                                    fileModel.name
                                }
                            } else {
                                files.sortedBy { fileModel ->
                                    fileModel.name
                                }
                            }
                        }
                        SortType.BySize -> {
                            if(it.listOrderType == OrderType.ByDescending) {
                                files.sortedByDescending { fileModel ->
                                    fileModel.size
                                }
                            } else {
                                files.sortedBy { fileModel ->
                                    fileModel.size
                                }
                            }
                        }
                    },
                    listSortType = sortType,
                    sortTypeDropDownMenuVisible = false
                )
            }
        }
    }

    fun onOrderTypeChipClick(dropDownMenuVisible: Boolean) {
        _state.update {
            it.copy(
                orderTypeDropDownMenuVisible = dropDownMenuVisible
            )
        }
    }

    fun onListOrderTypeChange(orderType: OrderType) {
        _state.update {
            it.copy(
                listOrderType = orderType,
                files = if(it.listOrderType != orderType) it.files.asReversed() else it.files,
                orderTypeDropDownMenuVisible = false
            )
        }
    }
}