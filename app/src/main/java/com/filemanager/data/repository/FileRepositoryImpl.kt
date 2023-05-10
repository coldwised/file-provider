package com.filemanager.data.repository

import android.webkit.MimeTypeMap
import com.filemanager.data.local.FileDao
import com.filemanager.domain.model.FileModel
import com.filemanager.domain.repository.FileRepository
import kotlinx.coroutines.*
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
            coroutineScope {
                val dao = dao
                val mimeTypeMap = MimeTypeMap.getSingleton()
                val resultFiles = mutableListOf<Deferred<FileModel>>()
                File(path).listFiles()?.sortedBy { it.name }?.forEach { file ->
                    resultFiles.add(async(Dispatchers.IO) {
                        var isChanged = false
                        val mimeType = if(file.isDirectory) {
                            "folder"
                        } else {
                            val filePath = file.path
                            val fileEntity = dao.getFileByPath(filePath)
                            if(fileEntity != null) {
                                isChanged = if(fileEntity.isChanged) true else {
                                    file.lastModified() != fileEntity.lastModified
                                }
                            }
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
                    })
                }
                emit(resultFiles.awaitAll())
            }
        }.flowOn(Dispatchers.IO)
    }
}