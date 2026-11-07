package com.ladamengh.petproject.app

import androidx.lifecycle.ViewModel
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class CatlasViewModel(
    private val catRepository: CatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatlasUiState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<CatlasEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        onIntent(CatlasIntent.LoadCats)
    }

    fun onIntent(intent: CatlasIntent) {
        when (intent) {
            CatlasIntent.LoadCats -> loadCats()
            is CatlasIntent.SelectTab -> effectChannel.trySend(CatlasEffect.NavigateToTab(intent.tab))
            is CatlasIntent.OpenCat -> effectChannel.trySend(CatlasEffect.NavigateToCat(intent.catId))
            is CatlasIntent.ToggleFavorite -> toggleFavorite(intent.catId)
        }
    }

    private fun loadCats() {
        _uiState.update { it.copy(isLoading = true) }
        val cats = catRepository.getCats()
        _uiState.update { it.copy(cats = cats, isLoading = false) }
    }

    private fun toggleFavorite(catId: Int) {
        _uiState.update { state ->
            val favoriteIds = if (catId in state.favoriteCatIds) {
                state.favoriteCatIds - catId
            } else {
                state.favoriteCatIds + catId
            }
            state.copy(favoriteCatIds = favoriteIds)
        }
    }
}
