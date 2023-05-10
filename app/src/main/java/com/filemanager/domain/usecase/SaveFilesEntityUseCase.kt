package com.filemanager.domain.usecase

import android.os.Environment
import com.filemanager.data.local.FileDao
import com.filemanager.data.local.entity.FileEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class SaveFilesEntityUseCase @Inject constructor(
    private val fileDao: FileDao
) {
    suspend operator fun invoke(): Flow<Unit> {
        return channelFlow {
            coroutineScope rootScope@{
                launch(Dispatchers.IO) {
                    fileDao.resetFiles()
                    send(Unit)
                }
                launch(Dispatchers.IO) {
                    saveAllFilesHashes(this, File(Environment.getExternalStorageDirectory().path))
                    send(Unit)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun saveAllFilesHashes(coroutineScope: CoroutineScope, rootDirectory: File) {
        val fileDao = fileDao
        rootDirectory.walkTopDown().forEach { file ->
            coroutineScope.launch {
                if (!file.isDirectory) {
                    val existingFile = fileDao.getFileByPath(file.path)
                    val lastModified = file.lastModified()
                    if(existingFile != null) {
                        fileDao.updateFile(existingFile.id, lastModified, existingFile.lastModified != lastModified)
                    } else {
                        val fileEntity = FileEntity(path = file.path, lastModified = lastModified)
                        fileDao.insertFile(fileEntity)
                    }
                }
            }
        }
    }
}