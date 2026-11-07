package com.ladamengh.petproject.app

import com.ladamengh.petproject.feature.navigation.AppTab

sealed interface CatlasIntent {
    data object LoadCats : CatlasIntent
    data class SelectTab(val tab: AppTab) : CatlasIntent
    data class OpenCat(val catId: Int) : CatlasIntent
    data class ToggleFavorite(val catId: Int) : CatlasIntent
}
