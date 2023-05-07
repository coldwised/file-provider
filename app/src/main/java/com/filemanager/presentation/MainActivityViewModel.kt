package com.filemanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filemanager.domain.usecase.SaveFilesEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val saveFilesEntityUseCase: SaveFilesEntityUseCase
): ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            saveFilesEntityUseCase()
        }
    }
}