package com.filemanager.domain.usecase

import android.os.Environment
import com.filemanager.domain.model.FileModel
import com.filemanager.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepository
) {
    operator fun invoke(path: String?): Flow<List<FileModel>> {
        return repository.getFiles(path ?: Environment.getExternalStorageDirectory().path)
    }
}