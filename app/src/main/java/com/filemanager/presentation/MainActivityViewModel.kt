package com.filemanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filemanager.domain.usecase.SaveFilesEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var saveHashCodesJob: Job? = null

    fun onChangePermissionStatus(permissionGranted: Boolean) {
        val state = _state
        if(state.value.permissionGranted != permissionGranted && permissionGranted) {
            saveHashCodesJob?.cancel()
            saveHashCodesJob = saveHashCodes()
        }
        _state.update {
            it.copy(
                permissionGranted = permissionGranted
            )
        }
    }

    init {
        if(state.value.permissionGranted) {
            saveHashCodesJob?.cancel()
            saveHashCodesJob = saveHashCodes()
        }
    }

    private fun saveHashCodes(): Job {
        return viewModelScope.launch {
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