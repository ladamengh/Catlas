package com.ladamengh.petproject.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.feature.navigation.AppTab
import com.ladamengh.petproject.ui.theme.CatlasColors

internal fun AppTab.toImageVector(): ImageVector? = when (this) {
    AppTab.Home -> Icons.Filled.Home
    AppTab.Catalog -> Icons.Filled.GridView
    AppTab.Camera -> Icons.Filled.PhotoCamera
    AppTab.Favorites -> Icons.Filled.Favorite
    AppTab.Profile -> null
}

internal fun Cat.artworkBackgroundColor(): Color = when (id % 3) {
    1 -> CatlasColors.artworkPeach
    2 -> CatlasColors.artworkSage
    else -> CatlasColors.artworkLilac
}
