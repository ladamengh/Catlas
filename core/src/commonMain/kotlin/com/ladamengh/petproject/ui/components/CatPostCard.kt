package com.ladamengh.petproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.cat_image_description
import com.ladamengh.petproject.resources.favorite_add
import com.ladamengh.petproject.resources.favorite_remove
import com.ladamengh.petproject.resources.cat_location_unknown
import com.ladamengh.petproject.resources.cat_name_unknown
import com.ladamengh.petproject.resources.cat_breed_unknown
import com.ladamengh.petproject.resources.ic_cat
import com.ladamengh.petproject.domain.model.Cat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.ladamengh.petproject.ui.theme.CatlasColors
import com.ladamengh.petproject.utils.artworkBackgroundColor

@Composable
fun CatPostCard(
    cat: Cat,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = CatlasColors.cardSurface,
        shadowElevation = 3.dp,
    ) {
        Column {
            CatPostImage(cat, isFavorite, onFavoriteClick)
            CatPostDetails(cat)
        }
    }
}

@Composable
private fun CatPostImage(
    cat: Cat,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    val catName = cat.name.ifBlank { stringResource(Res.string.cat_name_unknown) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp)
            .background(cat.artworkBackgroundColor()),
        contentAlignment = Alignment.Center,
    ) {
        if (!cat.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = cat.imageUrl,
                contentDescription = stringResource(Res.string.cat_image_description, catName),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(224.dp),
            )
        } else {
            CatArtworkPlaceholder(catName)
        }
        FavoriteButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(CircleShape)
                .background(CatlasColors.cardSurface),
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
        )
    }
}

@Composable
private fun CatArtworkPlaceholder(catName: String) {
    Surface(
        modifier = Modifier.size(148.dp),
        shape = CircleShape,
        color = CatlasColors.artworkOverlay,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cat),
            contentDescription = stringResource(Res.string.cat_image_description, catName),
            modifier = Modifier.padding(27.dp),
            tint = CatlasColors.artworkInk,
        )
    }
}

@Composable
private fun FavoriteButton(
    modifier: Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(
                if (isFavorite) Res.string.favorite_remove else Res.string.favorite_add,
            ),
            tint = if (isFavorite) CatlasColors.favorite else CatlasColors.favoriteInactive,
        )
    }
}

@Composable
private fun CatPostDetails(cat: Cat) {
    val catName = cat.name.ifBlank { stringResource(Res.string.cat_name_unknown) }
    val catBreed = cat.breed.ifBlank { stringResource(Res.string.cat_breed_unknown) }
    Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = catName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = CatlasColors.textPrimary,
            )
            Text(
                text = cat.location.ifBlank { stringResource(Res.string.cat_location_unknown) },
                style = MaterialTheme.typography.labelLarge,
                color = CatlasColors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            catBreed,
            style = MaterialTheme.typography.bodyMedium,
            color = CatlasColors.textTertiary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(cat.description, style = MaterialTheme.typography.bodyMedium, color = CatlasColors.textBody, maxLines = 2)
    }
}

