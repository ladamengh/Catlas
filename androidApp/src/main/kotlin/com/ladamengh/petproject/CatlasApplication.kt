package com.ladamengh.petproject

import android.app.Application
import com.ladamengh.petproject.data.local.createCatlasDatabase
import com.ladamengh.petproject.di.appModule
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

/**
 * Owns the Koin graph for the whole process.
 *
 * The graph must outlive Activity re-creation: ViewModels retained across configuration changes keep
 * references to repositories and the database, so closing Koin from a composition would leave them
 * pointing at closed resources.
 */
class CatlasApplication : Application() {
    val koinApp: KoinApplication by lazy {
        koinApplication {
            modules(appModule(createCatlasDatabase(this@CatlasApplication)))
        }
    }
}
