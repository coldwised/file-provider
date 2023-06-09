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

    @Query("SELECT EXISTS(SELECT 1 FROM $TABLE_NAME WHERE hash_code =:hashCode)")
    suspend fun hasItem(hashCode: Long): Boolean

    @Query("SELECT * FROM $TABLE_NAME WHERE path =:path LIMIT 1")
    suspend fun getFileByPath(path: String): FileEntity?

    @Query("UPDATE $TABLE_NAME SET hash_code =:hashCode, isChanged =:isChanged WHERE id =:id")
    suspend fun updateFile(id: Int, hashCode: Long, isChanged: Boolean)

    @Query("UPDATE $TABLE_NAME SET isChanged = 0")
    suspend fun resetFiles()

    companion object {
        const val TABLE_NAME = "files_table"
    }
}