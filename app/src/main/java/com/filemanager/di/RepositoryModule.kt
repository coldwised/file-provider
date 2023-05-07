package com.filemanager.di

import com.filemanager.data.repository.FileRepositoryImpl
import com.filemanager.domain.repository.FileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: FileRepositoryImpl
    ): FileRepository
}