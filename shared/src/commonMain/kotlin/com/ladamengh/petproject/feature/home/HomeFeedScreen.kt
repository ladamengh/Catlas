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
import com.ladamengh.petproject.app.CatlasIntent
import com.ladamengh.petproject.domain.model.Cat

@Composable
fun HomeFeedScreen(
    cats: List<Cat>,
    favoriteCatIds: Set<Int>,
    isLoading: Boolean,
    onIntent: (CatlasIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
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
        items(cats, key = Cat::id) { cat ->
            CatPostCard(
                cat = cat,
                isFavorite = cat.id in favoriteCatIds,
                onFavoriteClick = { onIntent(CatlasIntent.ToggleFavorite(cat.id)) },
                onClick = { onIntent(CatlasIntent.OpenCat(cat.id)) },
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
    }
}
