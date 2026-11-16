package com.ladamengh.petproject.utils

import androidx.compose.ui.graphics.Color
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.ui.theme.CatlasColors
import kotlin.random.Random

private const val RANDOM_ID_BYTE_COUNT = 16
private const val HEX_RADIX = 16
private const val HEX_BYTE_WIDTH = 2
private const val HEX_PADDING_CHARACTER = '0'
private const val ARTWORK_VARIANT_COUNT = 3

fun createRandomHexId(): String = Random.nextBytes(RANDOM_ID_BYTE_COUNT)
    .joinToString(separator = "") { byte ->
        byte.toUByte().toString(HEX_RADIX).padStart(HEX_BYTE_WIDTH, HEX_PADDING_CHARACTER)
    }

fun Cat.artworkBackgroundColor(): Color = when (id % ARTWORK_VARIANT_COUNT) {
    1 -> CatlasColors.artworkPeach
    2 -> CatlasColors.artworkSage
    else -> CatlasColors.artworkOlive
}
