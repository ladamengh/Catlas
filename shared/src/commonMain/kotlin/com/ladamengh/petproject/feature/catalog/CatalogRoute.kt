package com.ladamengh.petproject.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.flow.collect

@Composable
fun CatalogRoute(
    catRepository: CatRepository,
    onNavigateToBreed: (String) -> Unit,
) {
    val viewModel: CatalogViewModel = viewModel(key = "catalog") {
        CatalogViewModel(catRepository)
    }
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
