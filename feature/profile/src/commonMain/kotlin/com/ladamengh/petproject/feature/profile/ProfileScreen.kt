package com.ladamengh.petproject.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ladamengh.petproject.domain.model.UserProfile
import com.ladamengh.petproject.ui.theme.CatlasColors

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ProfileHeader(state, onIntent)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Avatar(
                image = if (state.isEditing) state.draftAvatarImage else state.profile.avatarImage,
                isEditing = state.isEditing,
                onClick = { onIntent(ProfileIntent.PickAvatar) },
            )
            if (state.isEditing) ProfileEditor(state, onIntent) else ProfileSummary(state.profile)
        }
    }
}

@Composable
private fun ProfileHeader(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CatlasColors.textPrimary,
        )
        TextButton(
            onClick = { onIntent(ProfileIntent.EditOrSave) },
            enabled = !state.isEditing || state.draftName.isNotBlank(),
        ) {
            if (state.isEditing) {
                Text("Сохранить")
            } else {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                Text("Редактировать")
            }
        }
    }
}

@Composable
private fun ProfileEditor(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    Text(
        text = "Нажмите на фото, чтобы изменить",
        style = MaterialTheme.typography.bodySmall,
        color = CatlasColors.textSecondary,
    )
    OutlinedTextField(
        value = state.draftName,
        onValueChange = { onIntent(ProfileIntent.ChangeName(it)) },
        label = { Text("Имя") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = state.draftCity,
        onValueChange = { onIntent(ProfileIntent.ChangeCity(it)) },
        label = { Text("Город (необязательно)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = state.draftDescription,
        onValueChange = { onIntent(ProfileIntent.ChangeDescription(it)) },
        label = { Text("О себе (необязательно)") },
        minLines = 3,
        maxLines = 5,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ProfileSummary(profile: UserProfile) {
    Text(
        text = profile.displayName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = CatlasColors.textPrimary,
        textAlign = TextAlign.Center,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = CatlasColors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = profile.city?.takeIf(String::isNotBlank)?.let { "Город: $it" } ?: "Город: не выбран",
            style = MaterialTheme.typography.bodyMedium,
            color = CatlasColors.textSecondary,
        )
    }
    profile.description?.takeIf(String::isNotBlank)?.let { description ->
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = CatlasColors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Avatar(
    image: ByteArray?,
    isEditing: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(144.dp)
            .clip(CircleShape)
            .then(if (isEditing) Modifier.clickable(onClick = onClick) else Modifier),
        shape = CircleShape,
        color = CatlasColors.artworkPeach,
        shadowElevation = 3.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = "Аватар пользователя",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = CatlasColors.textSecondary,
                    modifier = Modifier.size(72.dp),
                )
            }
        }
    }
}
