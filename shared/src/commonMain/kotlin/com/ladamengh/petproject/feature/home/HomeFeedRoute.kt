package com.ladamengh.petproject.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun HomeFeedRoute(
    onNavigateToCat: (Int) -> Unit,
) {
    val viewModel: HomeFeedViewModel = koinViewModel(key = "home-feed")
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
