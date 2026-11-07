package com.ladamengh.petproject.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.back_to_feed
import catlas.shared.generated.resources.cat_about
import catlas.shared.generated.resources.cat_image_description
import catlas.shared.generated.resources.favorite_add
import catlas.shared.generated.resources.favorite_breed_add
import catlas.shared.generated.resources.favorite_breed_remove
import catlas.shared.generated.resources.favorite_remove
import catlas.shared.generated.resources.ic_cat
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.ui.theme.CatlasColors
import com.ladamengh.petproject.utils.placeholderColor
import org.jetbrains.compose.resources.painterResource
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(cat.placeholderColor()),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cat),
                    contentDescription = stringResource(Res.string.cat_image_description, cat.name),
                    modifier = Modifier.size(184.dp),
                    tint = CatlasColors.artworkInk,
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(CatlasColors.cardSurface),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_to_feed),
                        tint = CatlasColors.textPrimary,
                    )
                }
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(CatlasColors.cardSurface),
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(
                            if (isFavorite) Res.string.favorite_remove else Res.string.favorite_add,
                        ),
                        tint = if (isFavorite) CatlasColors.favorite else CatlasColors.favoriteInactive,
                    )
                }
            }

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
