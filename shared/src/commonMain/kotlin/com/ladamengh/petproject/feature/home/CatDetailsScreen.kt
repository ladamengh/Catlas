package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.layout.Column
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
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.cat_about
import catlas.shared.generated.resources.cat_image_description
import catlas.shared.generated.resources.favorite_add
import catlas.shared.generated.resources.favorite_breed_add
import catlas.shared.generated.resources.favorite_breed_remove
import catlas.shared.generated.resources.favorite_remove
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.feature.details.DetailsHeader
import com.ladamengh.petproject.ui.theme.CatlasColors
import com.ladamengh.petproject.utils.placeholderColor
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
                backgroundColor = cat.placeholderColor(),
                imageDescription = stringResource(Res.string.cat_image_description, cat.name),
                isFavorite = isFavorite,
                favoriteDescription = stringResource(
                    if (isFavorite) Res.string.favorite_remove else Res.string.favorite_add,
                ),
                onBack = onBack,
                onFavoriteClick = onFavoriteClick,
            )

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = cat.breed,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = CatlasColors.textTertiary,
                    )
                    IconButton(onClick = onBreedFavoriteClick) {
                        Icon(
                            imageVector = if (isBreedFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Filled.FavoriteBorder
                            },
                            contentDescription = stringResource(
                                if (isBreedFavorite) {
                                    Res.string.favorite_breed_remove
                                } else {
                                    Res.string.favorite_breed_add
                                },
                            ),
                            tint = if (isBreedFavorite) {
                                CatlasColors.favorite
                            } else {
                                CatlasColors.favoriteInactive
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = cat.location,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CatlasColors.textBody,
                )
            }
        }
    }
}
