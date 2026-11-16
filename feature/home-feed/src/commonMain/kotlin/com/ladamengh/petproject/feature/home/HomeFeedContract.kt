package com.ladamengh.petproject.feature.home

import com.ladamengh.petproject.domain.model.Cat

data class HomeFeedState(
    val cats: List<Cat> = emptyList(),
    val favoriteCatIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)

sealed interface HomeFeedIntent {
    data class ToggleFavorite(val catId: Int) : HomeFeedIntent
    data class OpenCat(val catId: Int) : HomeFeedIntent
    data object Retry : HomeFeedIntent
}

sealed interface HomeFeedEffect {
    data class NavigateToCat(val catId: Int) : HomeFeedEffect
}
