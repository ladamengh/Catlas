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

@Suppress("FunctionNaming") // Swift uses this exported symbol as the iOS app entry point.
fun MainViewController() = ComposeUIViewController {
    App(iosKoinApplication)
}
