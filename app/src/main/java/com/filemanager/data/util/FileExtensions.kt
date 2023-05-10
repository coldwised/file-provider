package com.filemanager.data.util

import java.io.File
import java.util.zip.Adler32

fun File.contentHashCode(): Long {
    val buffer = ByteArray(8192)
    val adler32 = Adler32()
    inputStream().use { input ->
        while (true) {
            val length = input.read(buffer)
            if (length == -1) {
                break
            }
            adler32.update(buffer, 0, length)
        }
    }
    return adler32.value
}