package com.ladamengh.petproject

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ladamengh.petproject.app.CatlasApp
import com.ladamengh.petproject.data.local.createCatlasDatabase

@Composable
fun App(platformContext: Any? = null) {
    val database = remember(platformContext) { createCatlasDatabase(platformContext) }
    CatlasApp(database = database)
}
