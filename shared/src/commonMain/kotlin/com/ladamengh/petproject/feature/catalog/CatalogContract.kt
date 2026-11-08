package com.ladamengh.petproject.feature.catalog

import com.ladamengh.petproject.domain.model.CatBreed

data class CatalogState(
    val breeds: List<CatBreed> = emptyList(),
    val favoriteBreeds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)

sealed interface CatalogIntent {
    data class ToggleFavoriteBreed(val breed: String) : CatalogIntent
    data class OpenBreed(val breedId: String) : CatalogIntent
    data object Retry : CatalogIntent
}

sealed interface CatalogEffect {
    data class NavigateToBreed(val breedId: String) : CatalogEffect
}
