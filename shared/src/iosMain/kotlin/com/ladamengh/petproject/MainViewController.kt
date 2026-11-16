package com.ladamengh.petproject

import androidx.compose.ui.window.ComposeUIViewController
import com.ladamengh.petproject.data.local.createCatlasDatabase
import com.ladamengh.petproject.di.appModule
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

private val iosKoinApplication: KoinApplication by lazy {
    koinApplication {
        modules(appModule(createCatlasDatabase()))
    }
}

fun MainViewController() = ComposeUIViewController {
    App(iosKoinApplication)
}
