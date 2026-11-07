package com.ladamengh.petproject.app

import com.ladamengh.petproject.feature.navigation.AppTab

sealed interface CatlasEffect {
    data class NavigateToTab(val tab: AppTab) : CatlasEffect
    data class NavigateToCat(val catId: Int) : CatlasEffect
}
