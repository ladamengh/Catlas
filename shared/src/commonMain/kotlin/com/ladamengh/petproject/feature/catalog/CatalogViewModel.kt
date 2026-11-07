package com.ladamengh.petproject.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val catRepository: CatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<CatalogEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                catRepository.breeds,
                catRepository.favoriteBreeds,
            ) { breeds, favoriteBreeds ->
                CatalogState(
                    breeds = breeds.sortedBy { it.name },
                    favoriteBreeds = favoriteBreeds,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: CatalogIntent) {
        when (intent) {
            is CatalogIntent.ToggleFavoriteBreed -> catRepository.toggleFavoriteBreed(intent.breed)
            is CatalogIntent.OpenBreed -> effectChannel.trySend(
                CatalogEffect.NavigateToBreed(intent.breedId),
            )
        }
    }
}
