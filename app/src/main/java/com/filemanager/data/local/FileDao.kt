package com.filemanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.filemanager.data.local.entity.FileEntity

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM $TABLE_NAME WHERE lastModified =:lastModified)")
    suspend fun hasItem(lastModified: Long): Boolean

    @Query("SELECT * FROM $TABLE_NAME WHERE path =:path LIMIT 1")
    suspend fun getFileByPath(path: String): FileEntity?

    @Query("UPDATE $TABLE_NAME SET lastModified =:lastModified, isChanged =:isChanged WHERE id =:id")
    suspend fun updateFile(id: Int, lastModified: Long, isChanged: Boolean)

    @Query("UPDATE $TABLE_NAME SET isChanged = 0")
    suspend fun resetFiles()

    companion object {
        const val TABLE_NAME = "files_table"
    }
}