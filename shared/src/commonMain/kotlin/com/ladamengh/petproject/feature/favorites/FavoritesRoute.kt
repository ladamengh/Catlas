package com.ladamengh.petproject.feature.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun FavoritesRoute(
    onNavigateToCat: (Int) -> Unit,
    onNavigateToBreed: (String) -> Unit,
) {
    val viewModel: FavoritesViewModel = koinViewModel(key = "favorites")
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
