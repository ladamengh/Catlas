package com.ladamengh.petproject.feature.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.flow.collect

@Composable
fun FavoritesRoute(
    catRepository: CatRepository,
    onNavigateToCat: (Int) -> Unit,
    onNavigateToBreed: (String) -> Unit,
) {
    val viewModel: FavoritesViewModel = viewModel(key = "favorites") {
        FavoritesViewModel(catRepository)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, onNavigateToCat, onNavigateToBreed) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoritesEffect.NavigateToCat -> onNavigateToCat(effect.catId)
                is FavoritesEffect.NavigateToBreed -> onNavigateToBreed(effect.breedId)
            }
        }
    }

    FavoritesScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
    )
}
