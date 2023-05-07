package com.filemanager.domain.repository

import com.filemanager.domain.model.FileModel
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    fun getFiles(path: String): Flow<List<FileModel>>
}