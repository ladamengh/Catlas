package com.ladamengh.petproject.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ladamengh.petproject.data.user.RoomUserDataStore
import com.ladamengh.petproject.data.user.SubmittedCatRecord
import com.ladamengh.petproject.ui.theme.CatlasColors

@Composable
fun ProfileRoute(store: RoomUserDataStore) {
    val profile by store.profile.collectAsState()
    val submittedCats by store.submittedCats.collectAsState()
    var displayName by remember(profile.id) { mutableStateOf(profile.displayName) }
    var isNameSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CatlasColors.textPrimary,
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CatlasColors.cardSurface,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Локальный профиль",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CatlasColors.textPrimary,
                )
                Text(
                    text = "Данные хранятся на этом устройстве и доступны после перезапуска приложения.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatlasColors.textSecondary,
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = {
                        displayName = it
                        isNameSaved = false
                    },
                    label = { Text("Имя") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Отправлено котиков: ${submittedCats.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CatlasColors.textSecondary,
                    )
                    Button(
                        onClick = {
                            store.saveDisplayName(displayName)
                            isNameSaved = true
                        },
                        enabled = displayName.isNotBlank(),
                    ) {
                        Text(if (isNameSaved) "Сохранено" else "Сохранить")
                    }
                }
            }
        }

        Text(
            text = "Отправленные котики",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = CatlasColors.textPrimary,
        )

        if (submittedCats.isEmpty()) {
            Text(
                text = "Здесь появятся котики, которых вы отправите.",
                style = MaterialTheme.typography.bodyMedium,
                color = CatlasColors.textSecondary,
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(submittedCats, key = SubmittedCatRecord::imageId) { cat ->
                    SubmittedCatItem(cat)
                }
            }
        }
    }
}

@Composable
private fun SubmittedCatItem(cat: SubmittedCatRecord) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CatlasColors.cardSurface,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = cat.breedName ?: "Котик",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CatlasColors.textPrimary,
            )
            Text(
                text = "ID: ${cat.imageId}",
                style = MaterialTheme.typography.bodySmall,
                color = CatlasColors.textSecondary,
            )
        }
    }
}
