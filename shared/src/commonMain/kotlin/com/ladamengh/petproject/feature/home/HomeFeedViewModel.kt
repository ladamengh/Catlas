package com.ladamengh.petproject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeFeedViewModel(
    private val catRepository: CatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeFeedState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<HomeFeedEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(catRepository.cats, catRepository.favoriteCatIds) { cats, favoriteCatIds ->
                HomeFeedState(
                    cats = cats,
                    favoriteCatIds = favoriteCatIds,
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: HomeFeedIntent) {
        when (intent) {
            is HomeFeedIntent.ToggleFavorite -> catRepository.toggleFavoriteCat(intent.catId)
            is HomeFeedIntent.OpenCat -> effectChannel.trySend(
                HomeFeedEffect.NavigateToCat(intent.catId),
            )
        }
    }
}
