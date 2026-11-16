package com.ladamengh.petproject.utils

import androidx.compose.ui.graphics.Color
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.ui.theme.CatlasColors

fun Cat.artworkBackgroundColor(): Color = when (id % 3) {
    1 -> CatlasColors.artworkPeach
    2 -> CatlasColors.artworkSage
    else -> CatlasColors.artworkLilac
}
