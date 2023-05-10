package com.filemanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filemanager.domain.usecase.SaveFilesEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val saveFilesEntityUseCase: SaveFilesEntityUseCase
): ViewModel() {

    private val _state = MutableStateFlow(MainActivityState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            saveFilesEntityUseCase().collect {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
}