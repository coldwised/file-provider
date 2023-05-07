package com.filemanager.domain.usecase

import android.os.Environment
import com.filemanager.data.local.FileDao
import com.filemanager.data.local.entity.FileEntity
import java.io.File
import javax.inject.Inject

class SaveFilesEntityUseCase @Inject constructor(
    private val fileDao: FileDao
) {
    suspend operator fun invoke() {
        saveAllFilesHashes(File(Environment.getExternalStorageDirectory().path))
    }

    private suspend fun saveAllFilesHashes(dir: File) {
        val files = dir.listFiles().orEmpty()
        for (file in files) {
            if (file.isDirectory) {
                saveAllFilesHashes(file)
            } else {
                val hashCode =  file.hashCode()
                val existingFile = fileDao.getFileByPath(file.path)
                if(existingFile != null) {
                    fileDao.updateFile(existingFile.id, hashCode, existingFile.hash_code != hashCode)
                } else {
                    val fileEntity = FileEntity(0, file.path, file.hashCode())
                    fileDao.insertFile(fileEntity)
                }
            }
        }
    }
}