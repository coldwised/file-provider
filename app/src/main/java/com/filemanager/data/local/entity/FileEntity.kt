package com.filemanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.filemanager.data.local.FileDao

@Entity(tableName = FileDao.TABLE_NAME)
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val path: String,
    val hash_code: Int,
    val isChanged: Boolean = false,
)