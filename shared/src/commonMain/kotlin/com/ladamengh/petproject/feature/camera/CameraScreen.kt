package com.ladamengh.petproject.feature.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.camera_capture_hint
import catlas.shared.generated.resources.camera_cat_breed_label
import catlas.shared.generated.resources.camera_cat_name_label
import catlas.shared.generated.resources.camera_choose_from_gallery
import catlas.shared.generated.resources.camera_city_label
import catlas.shared.generated.resources.camera_description_label
import catlas.shared.generated.resources.camera_details_title
import catlas.shared.generated.resources.camera_photo_description
import catlas.shared.generated.resources.camera_publish
import catlas.shared.generated.resources.camera_required_fields_error
import catlas.shared.generated.resources.camera_retake_photo
import catlas.shared.generated.resources.camera_save_error
import catlas.shared.generated.resources.camera_take_photo
import catlas.shared.generated.resources.camera_title
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun CameraScreen(
    state: CameraState,
    isCameraLoading: Boolean,
    onIntent: (CameraIntent) -> Unit,
    onTakePhoto: () -> Unit,
    onChoosePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val validationError = stringResource(Res.string.camera_required_fields_error)
    val saveError = stringResource(Res.string.camera_save_error)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.camera_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CatlasColors.textPrimary,
        )

        val photo = state.photo
        if (photo == null) {
            Box(
                modifier = Modifier.fillMaxWidth().height(360.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.height(80.dp),
                        tint = CatlasColors.favorite,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.camera_capture_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = CatlasColors.textSecondary,
                    )
                }
            }
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = onTakePhoto,
                enabled = !isCameraLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isCameraLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.camera_take_photo))
                }
            }
            OutlinedButton(
                onClick = onChoosePhoto,
                enabled = !isCameraLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(Res.string.camera_choose_from_gallery))
            }
        } else {
            AsyncImage(
                model = photo,
                contentDescription = stringResource(Res.string.camera_photo_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(300.dp),
            )
            OutlinedButton(
                onClick = { onIntent(CameraIntent.RetakePhoto); onTakePhoto() },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(Res.string.camera_retake_photo))
            }
            Text(
                text = stringResource(Res.string.camera_details_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = CatlasColors.textPrimary,
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = { onIntent(CameraIntent.ChangeName(it)) },
                label = { Text(stringResource(Res.string.camera_cat_name_label)) },
                singleLine = true,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.breed,
                onValueChange = { onIntent(CameraIntent.ChangeBreed(it)) },
                label = { Text(stringResource(Res.string.camera_cat_breed_label)) },
                singleLine = true,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.city,
                onValueChange = { onIntent(CameraIntent.ChangeCity(it)) },
                label = { Text(stringResource(Res.string.camera_city_label)) },
                singleLine = true,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { onIntent(CameraIntent.ChangeDescription(it)) },
                label = { Text(stringResource(Res.string.camera_description_label)) },
                minLines = 3,
                maxLines = 5,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = {
                    onIntent(
                        CameraIntent.Save(
                            validationError = validationError,
                            saveError = saveError,
                        ),
                    )
                },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.camera_publish))
                }
            }
        }
    }
}
