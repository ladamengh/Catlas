package com.ladamengh.petproject.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BreedDetailsViewModel(
    private val breedId: String,
    private val catRepository: CatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BreedDetailsState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(catRepository.breeds, catRepository.favoriteBreeds) { breeds, favoriteBreeds ->
                val breed = breeds.firstOrNull { it.id == breedId }
                BreedDetailsState(
                    breed = breed,
                    isFavorite = breed?.name in favoriteBreeds,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: BreedDetailsIntent) {
        val breed = _uiState.value.breed ?: return
        when (intent) {
            BreedDetailsIntent.ToggleFavorite -> catRepository.toggleFavoriteBreed(breed.name)
        }
    }
}
