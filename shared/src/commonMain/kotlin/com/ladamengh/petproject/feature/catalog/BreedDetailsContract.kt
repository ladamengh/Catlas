package com.ladamengh.petproject.feature.catalog

import com.ladamengh.petproject.domain.model.CatBreed

data class BreedDetailsState(
    val breed: CatBreed? = null,
    val isFavorite: Boolean = false,
)

sealed interface BreedDetailsIntent {
    data object ToggleFavorite : BreedDetailsIntent
}
