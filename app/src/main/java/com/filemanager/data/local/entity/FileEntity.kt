package com.filemanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.filemanager.data.local.FileDao

@Entity(tableName = FileDao.TABLE_NAME)
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val lastModified: Long,
    val isChanged: Boolean = false,
)