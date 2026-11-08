package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.home_subtitle
import catlas.shared.generated.resources.home_title
import catlas.shared.generated.resources.home_feed_empty
import catlas.shared.generated.resources.home_feed_load_error
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.jetbrains.compose.resources.stringResource

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
        item {
            Column(modifier = Modifier.padding(bottom = 2.dp)) {
                Text(
                    text = stringResource(Res.string.home_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CatlasColors.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatlasColors.textSecondary,
                )
            }
        }
        if (state.hasError) {
            item {
                Button(
                    onClick = { onIntent(HomeFeedIntent.Retry) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.home_feed_load_error))
                }
            }
        }
        if (state.cats.isEmpty() && !state.hasError) {
            item {
                Text(
                    text = stringResource(Res.string.home_feed_empty),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CatlasColors.textSecondary,
                )
            }
        }
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
