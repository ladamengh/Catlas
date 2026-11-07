package com.ladamengh.petproject.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val catRepository: CatRepository,
) : ViewModel() {
    private val selectedSection = MutableStateFlow(FavoritesSection.Cats)

    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<FavoritesEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                catRepository.cats,
                catRepository.favoriteCatIds,
                catRepository.favoriteBreeds,
                selectedSection,
            ) { cats, favoriteCatIds, favoriteBreeds, section ->
                FavoritesState(
                    cats = cats,
                    favoriteCatIds = favoriteCatIds,
                    favoriteBreeds = favoriteBreeds,
                    selectedSection = section,
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.SelectSection -> selectedSection.value = intent.section
            is FavoritesIntent.ToggleFavoriteCat -> catRepository.toggleFavoriteCat(intent.catId)
            is FavoritesIntent.ToggleFavoriteBreed -> catRepository.toggleFavoriteBreed(intent.breed)
            is FavoritesIntent.OpenCat -> effectChannel.trySend(
                FavoritesEffect.NavigateToCat(intent.catId),
            )
        }
    }
}
