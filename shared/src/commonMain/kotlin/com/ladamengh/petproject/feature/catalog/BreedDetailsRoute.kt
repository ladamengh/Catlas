package com.ladamengh.petproject.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ladamengh.petproject.domain.repository.CatRepository

@Composable
fun BreedDetailsRoute(
    breedId: String,
    catRepository: CatRepository,
    onBack: () -> Unit,
) {
    val viewModel: BreedDetailsViewModel = viewModel(key = "breed-details-$breedId") {
        BreedDetailsViewModel(breedId, catRepository)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BreedDetailsScreen(
        state = uiState,
        onBack = onBack,
        onFavoriteClick = { viewModel.onIntent(BreedDetailsIntent.ToggleFavorite) },
    )
}
