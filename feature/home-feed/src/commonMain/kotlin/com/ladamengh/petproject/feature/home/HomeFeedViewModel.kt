package com.ladamengh.petproject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CancellationException
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
                cats to favoriteCatIds
            }.collect { (cats, favoriteCatIds) ->
                _uiState.value = _uiState.value.copy(cats = cats, favoriteCatIds = favoriteCatIds)
            }
        }
        loadCats()
    }

    fun onIntent(intent: HomeFeedIntent) {
        when (intent) {
            is HomeFeedIntent.ToggleFavorite -> catRepository.toggleFavoriteCat(intent.catId)
            is HomeFeedIntent.OpenCat -> effectChannel.trySend(
                HomeFeedEffect.NavigateToCat(intent.catId),
            )
            HomeFeedIntent.Retry -> loadCats()
        }
    }

    private fun loadCats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, hasError = false)
            try {
                catRepository.refreshCats()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                println("Catlas home feed request failed: ${exception.message}")
                _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
            }
        }
    }
}
