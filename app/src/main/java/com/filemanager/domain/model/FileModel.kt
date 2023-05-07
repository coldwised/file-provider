package com.filemanager.domain.model

import java.util.Date

data class FileModel(
    val isChanged: Boolean,
    val name: String,
    val path: String,
    val type: String,
    val size: Long,
    val creationDate: Date
)
