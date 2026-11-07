package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ladamengh.petproject.domain.model.Cat

@Composable
fun HomeFeedScreen(
    state: HomeFeedState,
    onIntent: (HomeFeedIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 20.dp,
            end = 20.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(state.cats, key = Cat::id) { cat ->
            CatPostCard(
                cat = cat,
                isFavorite = cat.id in state.favoriteCatIds,
                onFavoriteClick = { onIntent(HomeFeedIntent.ToggleFavorite(cat.id)) },
                onClick = { onIntent(HomeFeedIntent.OpenCat(cat.id)) },
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
    }
}
