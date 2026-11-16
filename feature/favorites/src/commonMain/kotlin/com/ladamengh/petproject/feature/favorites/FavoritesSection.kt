package com.ladamengh.petproject.feature.favorites

import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.favorites_breeds
import com.ladamengh.petproject.resources.favorites_cats
import org.jetbrains.compose.resources.StringResource

enum class FavoritesSection(val title: StringResource) {
    Cats(Res.string.favorites_cats),
    Breeds(Res.string.favorites_breeds),
}
