package com.ladamengh.petproject.feature.favorites

import com.ladamengh.petproject.domain.model.Cat

data class FavoritesState(
    val cats: List<Cat> = emptyList(),
    val favoriteCatIds: Set<Int> = emptySet(),
    val favoriteBreeds: Set<String> = emptySet(),
    val selectedSection: FavoritesSection = FavoritesSection.Cats,
    val isLoading: Boolean = true,
)

sealed interface FavoritesIntent {
    data class SelectSection(val section: FavoritesSection) : FavoritesIntent
    data class ToggleFavoriteCat(val catId: Int) : FavoritesIntent
    data class ToggleFavoriteBreed(val breed: String) : FavoritesIntent
    data class OpenCat(val catId: Int) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class NavigateToCat(val catId: Int) : FavoritesEffect
}
