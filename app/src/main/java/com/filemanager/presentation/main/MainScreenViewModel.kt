package com.filemanager.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filemanager.domain.usecase.GetFilesUseCase
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
        viewModelScope.launch {
            getFilesUseCase(path).collect { files ->
                _state.update {
                    it.copy(
                        files = files,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onChangePermissionDialogVisibility(visible: Boolean) {
        _state.update {
            it.copy(
                isPermissionDialogVisible = visible
            )
        }
    }
}