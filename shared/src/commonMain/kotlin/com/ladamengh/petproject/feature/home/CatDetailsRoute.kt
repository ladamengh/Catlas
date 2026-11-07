package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CatDetailsRoute(
    catId: Int,
    onBack: () -> Unit,
) {
    val viewModel: CatDetailsViewModel = koinViewModel(key = "cat-details-$catId") {
        parametersOf(catId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cat = uiState.cat ?: return

    CatDetailsScreen(
        cat = cat,
        isFavorite = uiState.isFavorite,
        isBreedFavorite = uiState.isBreedFavorite,
        onBack = onBack,
        onFavoriteClick = { viewModel.onIntent(CatDetailsIntent.ToggleFavorite) },
        onBreedFavoriteClick = { viewModel.onIntent(CatDetailsIntent.ToggleBreedFavorite) },
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    )
}
