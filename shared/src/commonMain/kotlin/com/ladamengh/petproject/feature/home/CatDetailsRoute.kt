package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ladamengh.petproject.domain.repository.CatRepository

@Composable
fun CatDetailsRoute(
    catId: Int,
    catRepository: CatRepository,
    onBack: () -> Unit,
) {
    val viewModel: CatDetailsViewModel = viewModel(key = "cat-details-$catId") {
        CatDetailsViewModel(catId, catRepository)
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
