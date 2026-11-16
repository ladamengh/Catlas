package com.ladamengh.petproject.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.breed_about
import com.ladamengh.petproject.resources.breed_image_description
import com.ladamengh.petproject.resources.breed_origin
import com.ladamengh.petproject.resources.breed_temperament
import com.ladamengh.petproject.resources.favorite_breed_add
import com.ladamengh.petproject.resources.favorite_breed_remove
import com.ladamengh.petproject.ui.components.DetailsHeader
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.jetbrains.compose.resources.stringResource

private const val ARTWORK_VARIANT_COUNT = 3

@Composable
fun BreedDetailsScreen(
    state: BreedDetailsState,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val breed = state.breed
    if (breed == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Surface(
        modifier = modifier.statusBarsPadding(),
        color = CatlasColors.screenBackground,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailsHeader(
                backgroundColor = breed.id.artworkBackgroundColor(),
                imageDescription = stringResource(Res.string.breed_image_description, breed.name),
                isFavorite = state.isFavorite,
                favoriteDescription = stringResource(
                    if (state.isFavorite) {
                        Res.string.favorite_breed_remove
                    } else {
                        Res.string.favorite_breed_add
                    },
                ),
                onBack = onBack,
                onFavoriteClick = onFavoriteClick,
                imageUrl = breed.imageUrl,
            )
            BreedDetailsContent(breed)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.BreedDetailsContent(breed: CatBreed) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = breed.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CatlasColors.textPrimary,
        )
        Spacer(Modifier.height(24.dp))
        BreedDetailSection(stringResource(Res.string.breed_origin), breed.origin)
        Spacer(Modifier.height(20.dp))
        BreedDetailSection(stringResource(Res.string.breed_temperament), breed.temperament)
        Spacer(Modifier.height(20.dp))
        BreedDetailSection(stringResource(Res.string.breed_about), breed.description)
    }
}

@Composable
private fun BreedDetailSection(
    title: String,
    value: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = CatlasColors.textPrimary,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge,
        color = CatlasColors.textBody,
    )
}

private fun String.artworkBackgroundColor() = when (hashCode().mod(ARTWORK_VARIANT_COUNT)) {
    0 -> CatlasColors.artworkPeach
    1 -> CatlasColors.artworkSage
    else -> CatlasColors.artworkLilac
}
