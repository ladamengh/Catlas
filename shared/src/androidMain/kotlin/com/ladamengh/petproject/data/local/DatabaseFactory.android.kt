package com.ladamengh.petproject.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getCatlasDatabaseBuilder(context: Any?): RoomDatabase.Builder<CatlasDatabase> {
    val appContext = requireNotNull(context as? Context) {
        "Android Context is required to create the Catlas database."
    }.applicationContext
    val databaseFile = appContext.getDatabasePath("catlas.db")
    return Room.databaseBuilder<CatlasDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}
