package com.ladamengh.petproject.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BreedDetailsRoute(
    breedId: String,
    onBack: () -> Unit,
) {
    val viewModel: BreedDetailsViewModel = koinViewModel(key = "breed-details-$breedId") {
        parametersOf(breedId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BreedDetailsScreen(
        state = uiState,
        onBack = onBack,
        onFavoriteClick = { viewModel.onIntent(BreedDetailsIntent.ToggleFavorite) },
    )
}
