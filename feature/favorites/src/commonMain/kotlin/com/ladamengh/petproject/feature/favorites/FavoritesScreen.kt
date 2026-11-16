package com.ladamengh.petproject.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.favorite_breed_label
import com.ladamengh.petproject.resources.favorite_breed_remove
import com.ladamengh.petproject.resources.favorites_empty_breeds_message
import com.ladamengh.petproject.resources.favorites_empty_breeds_title
import com.ladamengh.petproject.resources.favorites_empty_cats_message
import com.ladamengh.petproject.resources.favorites_empty_cats_title
import com.ladamengh.petproject.resources.favorites_title
import com.ladamengh.petproject.resources.ic_cat
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.ui.components.CatPostCard
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(Res.string.favorites_title),
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CatlasColors.textPrimary,
        )

        FavoritesSectionSelector(
            selectedSection = state.selectedSection,
            onSectionSelected = { onIntent(FavoritesIntent.SelectSection(it)) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            when (state.selectedSection) {
                FavoritesSection.Cats -> FavoriteCatsList(
                    cats = state.cats.filter { it.id in state.favoriteCatIds },
                    onFavoriteClick = { onIntent(FavoritesIntent.ToggleFavoriteCat(it)) },
                    onCatClick = { onIntent(FavoritesIntent.OpenCat(it)) },
                    modifier = Modifier.weight(1f),
                )

                FavoritesSection.Breeds -> FavoriteBreedsList(
                    favoriteBreeds = state.favoriteBreeds,
                    onFavoriteClick = { onIntent(FavoritesIntent.ToggleFavoriteBreed(it)) },
                    onBreedClick = { onIntent(FavoritesIntent.OpenBreed(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FavoritesSectionSelector(
    selectedSection: FavoritesSection,
    onSectionSelected: (FavoritesSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CatlasColors.segmentTrack, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FavoritesSection.entries.forEach { section ->
            val isSelected = section == selectedSection
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = null,
                        indication = null,
                    ) { onSectionSelected(section) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) CatlasColors.cardSurface else Color.Transparent,
                shadowElevation = if (isSelected) 1.dp else 0.dp,
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(section.title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            CatlasColors.textPrimary
                        } else {
                            CatlasColors.textSecondary
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCatsList(
    cats: List<Cat>,
    onFavoriteClick: (Int) -> Unit,
    onCatClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (cats.isEmpty()) {
        EmptyFavoritesContent(
            title = Res.string.favorites_empty_cats_title,
            message = Res.string.favorites_empty_cats_message,
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            top = 4.dp,
            end = 20.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(cats, key = Cat::id) { cat ->
            CatPostCard(
                cat = cat,
                isFavorite = true,
                onFavoriteClick = { onFavoriteClick(cat.id) },
                onClick = { onCatClick(cat.id) },
            )
        }
    }
}

@Composable
private fun FavoriteBreedsList(
    favoriteBreeds: Set<String>,
    onFavoriteClick: (String) -> Unit,
    onBreedClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (favoriteBreeds.isEmpty()) {
        EmptyFavoritesContent(
            title = Res.string.favorites_empty_breeds_title,
            message = Res.string.favorites_empty_breeds_message,
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            top = 4.dp,
            end = 20.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(favoriteBreeds.sorted(), key = { it }) { breed ->
            FavoriteBreedCard(
                breed = breed,
                onRemove = { onFavoriteClick(breed) },
                onClick = { onBreedClick(breed) },
            )
        }
    }
}

@Composable
private fun FavoriteBreedCard(breed: String, onRemove: () -> Unit, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = CatlasColors.cardSurface,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 18.dp, top = 14.dp, end = 8.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = CatlasColors.artworkPeach,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cat),
                    contentDescription = null,
                    modifier = Modifier.padding(11.dp),
                    tint = CatlasColors.artworkInk,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(
                    text = breed,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CatlasColors.textPrimary,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(Res.string.favorite_breed_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = CatlasColors.textSecondary,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(Res.string.favorite_breed_remove),
                    tint = CatlasColors.favorite,
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesContent(
    title: StringResource,
    message: StringResource,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = CatlasColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyMedium,
            color = CatlasColors.textSecondary,
        )
    }
}
