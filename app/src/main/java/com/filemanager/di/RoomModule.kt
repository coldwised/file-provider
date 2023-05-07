package com.filemanager.di

import android.app.Application
import androidx.room.Room
import com.filemanager.data.local.FileDao
import com.filemanager.data.local.FileDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): FileDatabase {
        return Room.databaseBuilder(
            app, FileDatabase::class.java, FileDatabase.name
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideImageUrlDao(
        db: FileDatabase,
    ): FileDao {
        return db.fileDao
    }
}