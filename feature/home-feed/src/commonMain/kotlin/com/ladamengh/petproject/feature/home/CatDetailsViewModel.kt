package com.ladamengh.petproject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CatDetailsViewModel(
    private val catId: Int,
    private val catRepository: CatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatDetailsState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                catRepository.cats,
                catRepository.favoriteCatIds,
                catRepository.favoriteBreeds,
            ) { cats, favoriteCatIds, favoriteBreeds ->
                val cat = cats.firstOrNull { it.id == catId }
                CatDetailsState(
                    cat = cat,
                    isFavorite = catId in favoriteCatIds,
                    isBreedFavorite = cat?.breed in favoriteBreeds,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: CatDetailsIntent) {
        val cat = _uiState.value.cat ?: return
        when (intent) {
            CatDetailsIntent.ToggleFavorite -> catRepository.toggleFavoriteCat(cat.id)
            CatDetailsIntent.ToggleBreedFavorite -> catRepository.toggleFavoriteBreed(cat.breed)
        }
    }
}
