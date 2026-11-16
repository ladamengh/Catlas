package com.ladamengh.petproject.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun CatalogRoute(
    onNavigateToBreed: (String) -> Unit,
) {
    val viewModel: CatalogViewModel = koinViewModel(key = "catalog")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, onNavigateToBreed) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CatalogEffect.NavigateToBreed -> onNavigateToBreed(effect.breedId)
            }
        }
    }

    CatalogScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
    )
}
