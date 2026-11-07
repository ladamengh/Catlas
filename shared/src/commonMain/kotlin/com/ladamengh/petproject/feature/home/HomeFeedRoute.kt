package com.ladamengh.petproject.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.flow.collect

@Composable
fun HomeFeedRoute(
    catRepository: CatRepository,
    onNavigateToCat: (Int) -> Unit,
) {
    val viewModel: HomeFeedViewModel = viewModel(key = "home-feed") {
        HomeFeedViewModel(catRepository)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, onNavigateToCat) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeFeedEffect.NavigateToCat -> onNavigateToCat(effect.catId)
            }
        }
    }

    HomeFeedScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
    )
}
