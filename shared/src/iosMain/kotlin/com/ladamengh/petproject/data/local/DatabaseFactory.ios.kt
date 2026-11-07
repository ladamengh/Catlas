package com.ladamengh.petproject.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun getCatlasDatabaseBuilder(context: Any?): RoomDatabase.Builder<CatlasDatabase> {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: error("Unable to find the app documents directory.")

    return Room.databaseBuilder<CatlasDatabase>(name = "$directory/catlas.db")
}
