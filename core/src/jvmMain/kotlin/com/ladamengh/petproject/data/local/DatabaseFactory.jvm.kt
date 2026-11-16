package com.ladamengh.petproject.data.local

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getCatlasDatabaseBuilder(context: Any?): RoomDatabase.Builder<CatlasDatabase> =
    Room.databaseBuilder<CatlasDatabase>(name = context as? String ?: "catlas.db")
