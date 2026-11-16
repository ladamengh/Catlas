package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.cat_about
import com.ladamengh.petproject.resources.cat_image_description
import com.ladamengh.petproject.resources.cat_location_unknown
import com.ladamengh.petproject.resources.favorite_add
import com.ladamengh.petproject.resources.favorite_breed_add
import com.ladamengh.petproject.resources.favorite_breed_remove
import com.ladamengh.petproject.resources.favorite_remove
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.ui.components.DetailsHeader
import com.ladamengh.petproject.ui.theme.CatlasColors
import com.ladamengh.petproject.utils.artworkBackgroundColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun CatDetailsScreen(
    cat: Cat,
    isFavorite: Boolean,
    isBreedFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBreedFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = CatlasColors.screenBackground,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailsHeader(
                backgroundColor = cat.artworkBackgroundColor(),
                imageDescription = stringResource(Res.string.cat_image_description, cat.name),
                isFavorite = isFavorite,
                favoriteDescription = stringResource(
                    if (isFavorite) Res.string.favorite_remove else Res.string.favorite_add,
                ),
                onBack = onBack,
                onFavoriteClick = onFavoriteClick,
                imageUrl = cat.imageUrl,
            )

            CatDetailsContent(cat, isBreedFavorite, onBreedFavoriteClick)
        }
    }
}

@Composable
private fun ColumnScope.CatDetailsContent(
    cat: Cat,
    isBreedFavorite: Boolean,
    onBreedFavoriteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp),
    ) {
        Text(
            text = cat.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CatlasColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CatBreedRow(cat.breed, isBreedFavorite, onBreedFavoriteClick)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cat.location.ifBlank { stringResource(Res.string.cat_location_unknown) },
            style = MaterialTheme.typography.bodyLarge,
            color = CatlasColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(Res.string.cat_about),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = CatlasColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cat.description,
            modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            style = MaterialTheme.typography.bodyLarge,
            color = CatlasColors.textBody,
        )
    }
}

@Composable
private fun CatBreedRow(
    breed: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = breed,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = CatlasColors.textTertiary,
        )
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = stringResource(
                    if (isFavorite) Res.string.favorite_breed_remove else Res.string.favorite_breed_add,
                ),
                tint = if (isFavorite) CatlasColors.favorite else CatlasColors.favoriteInactive,
            )
        }
    }
}
