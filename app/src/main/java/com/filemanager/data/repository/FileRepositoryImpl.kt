package com.filemanager.data.repository

import android.webkit.MimeTypeMap
import com.filemanager.data.local.FileDao
import com.filemanager.data.local.entity.FileEntity
import com.filemanager.data.util.contentHashCode
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
                var isChanged = false
                val resultFiles = mutableListOf<Deferred<FileModel>>()
                File(path).listFiles()?.forEach { file ->
                    resultFiles.add(async(Dispatchers.IO) {
                        val mimeType = if(file.isDirectory) {
                            "folder"
                        } else {
                            val filePath = file.path
                            val fileEntity = dao.getFileByPath(filePath)
                            if(fileEntity == null) {
                                dao.insertFile(
                                    FileEntity(
                                        path = filePath,
                                        hash_code = file.contentHashCode(),
                                    )
                                )
                            } else {
                                isChanged = if(fileEntity.isChanged) true else {
                                    file.contentHashCode() != fileEntity.hash_code
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