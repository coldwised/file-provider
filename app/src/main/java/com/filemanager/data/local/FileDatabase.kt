package com.filemanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.filemanager.data.local.entity.FileEntity

@Database(entities = [FileEntity::class], version = 1)
abstract class FileDatabase : RoomDatabase() {
    abstract val fileDao: FileDao

    companion object {
        const val name = "local_db"
    }
}