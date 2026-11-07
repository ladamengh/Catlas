package com.ladamengh.petproject.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

expect fun getCatlasDatabaseBuilder(context: Any? = null): RoomDatabase.Builder<CatlasDatabase>

fun createCatlasDatabase(context: Any? = null): CatlasDatabase =
    getCatlasDatabaseBuilder(context)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
