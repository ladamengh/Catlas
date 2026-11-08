package com.ladamengh.petproject.feature.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.breed_image_description
import catlas.shared.generated.resources.favorite_breed_add
import catlas.shared.generated.resources.favorite_breed_remove
import catlas.shared.generated.resources.ic_cat
import catlas.shared.generated.resources.catalog_subtitle
import catlas.shared.generated.resources.catalog_title
import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CatalogScreen(
    state: CatalogState,
    onIntent: (CatalogIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(
                    text = stringResource(Res.string.catalog_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CatlasColors.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.catalog_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatlasColors.textSecondary,
                )
            }
        }

        if (state.isLoading && state.breeds.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CatlasColors.favorite)
                }
            }
        }

        if (state.hasError) {
            item {
                Button(
                    onClick = { onIntent(CatalogIntent.Retry) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Не удалось загрузить каталог. Повторить")
                }
            }
        }

        items(state.breeds.chunked(2), key = { row -> row.first().id }) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { breed ->
                    BreedCard(
                        breed = breed,
                        isFavorite = breed.name in state.favoriteBreeds,
                        onClick = { onIntent(CatalogIntent.OpenBreed(breed.id)) },
                        onFavoriteClick = {
                            onIntent(CatalogIntent.ToggleFavoriteBreed(breed.name))
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BreedCard(
    breed: CatBreed,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = CatlasColors.cardSurface,
        shadowElevation = 2.dp,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .background(breed.id.placeholderColor()),
                contentAlignment = Alignment.Center,
            ) {
                if (!breed.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = breed.imageUrl,
                        contentDescription = stringResource(Res.string.breed_image_description, breed.name),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(92.dp),
                        shape = CircleShape,
                        color = CatlasColors.artworkOverlay,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cat),
                            contentDescription = null,
                            modifier = Modifier.padding(18.dp),
                            tint = CatlasColors.artworkInk,
                        )
                    }
                }
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CatlasColors.cardSurface),
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(
                            if (isFavorite) {
                                Res.string.favorite_breed_remove
                            } else {
                                Res.string.favorite_breed_add
                            },
                        ),
                        modifier = Modifier.size(20.dp),
                        tint = if (isFavorite) CatlasColors.favorite else CatlasColors.favoriteInactive,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = breed.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CatlasColors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun String.placeholderColor() = when (hashCode().mod(3)) {
    0 -> CatlasColors.artworkPeach
    1 -> CatlasColors.artworkSage
    else -> CatlasColors.artworkLilac
}
