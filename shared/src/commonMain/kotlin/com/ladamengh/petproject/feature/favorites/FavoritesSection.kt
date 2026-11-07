package com.ladamengh.petproject.feature.favorites

import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.favorites_breeds
import catlas.shared.generated.resources.favorites_cats
import org.jetbrains.compose.resources.StringResource

enum class FavoritesSection(val title: StringResource) {
    Cats(Res.string.favorites_cats),
    Breeds(Res.string.favorites_breeds),
}
