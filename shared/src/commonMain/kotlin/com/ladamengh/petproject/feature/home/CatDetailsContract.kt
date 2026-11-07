package com.ladamengh.petproject.feature.home

import com.ladamengh.petproject.domain.model.Cat

data class CatDetailsState(
    val cat: Cat? = null,
    val isFavorite: Boolean = false,
    val isBreedFavorite: Boolean = false,
)

sealed interface CatDetailsIntent {
    data object ToggleFavorite : CatDetailsIntent
    data object ToggleBreedFavorite : CatDetailsIntent
}
