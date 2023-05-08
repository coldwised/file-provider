package com.filemanager.data.repository

import android.webkit.MimeTypeMap
import com.filemanager.data.local.FileDao
import com.filemanager.domain.model.FileModel
import com.filemanager.domain.repository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.util.*
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val dao: FileDao,
): FileRepository {
    override fun getFiles(path: String): Flow<List<FileModel>> {
        return flow {

            val mimeTypeMap = MimeTypeMap.getSingleton()
            var isChanged = false
            val files = File(path).listFiles()?.map { file ->
                val mimeType = if(file.isDirectory) {
                    "folder"
                } else {
                    isChanged = dao.getFileByPath(file.path)?.isChanged ?: false
                    mimeTypeMap
                        .getMimeTypeFromExtension(file.extension)
                        ?.substringBefore('/') ?: "file"
                }
                FileModel(
                    isChanged = isChanged,
                    name = file.name,
                    type = mimeType,
                    size = file.length(),
                    creationDate = Date(file.lastModified()),
                    path = file.path
                )
            }.orEmpty()

            emit(files)
        }.flowOn(Dispatchers.IO)
    }
}