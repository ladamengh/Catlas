package com.ladamengh.petproject

import androidx.compose.runtime.Composable
import com.ladamengh.petproject.app.CatlasApp
import org.koin.core.KoinApplication

@Composable
fun App(koinApplication: KoinApplication) {
    CatlasApp(koinApplication)
}
