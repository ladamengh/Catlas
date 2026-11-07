package com.ladamengh.petproject.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.back_to_feed
import catlas.shared.generated.resources.favorite_add
import catlas.shared.generated.resources.favorite_remove
import catlas.shared.generated.resources.ic_cat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import com.ladamengh.petproject.ui.theme.CatlasColors

@Composable
fun DetailsHeader(
    backgroundColor: Color,
    imageDescription: String,
    isFavorite: Boolean,
    favoriteDescription: String,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cat),
            contentDescription = imageDescription,
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
                contentDescription = favoriteDescription,
                tint = if (isFavorite) CatlasColors.favorite else CatlasColors.favoriteInactive,
            )
        }
    }
}
