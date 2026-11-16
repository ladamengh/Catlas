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
import androidx.compose.material3.Icon
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
import com.ladamengh.petproject.resources.Res
import com.ladamengh.petproject.resources.camera_capture_hint
import com.ladamengh.petproject.resources.camera_cat_breed_label
import com.ladamengh.petproject.resources.camera_cat_name_label
import com.ladamengh.petproject.resources.camera_choose_from_gallery
import com.ladamengh.petproject.resources.camera_city_label
import com.ladamengh.petproject.resources.camera_description_label
import com.ladamengh.petproject.resources.camera_details_title
import com.ladamengh.petproject.resources.camera_photo_description
import com.ladamengh.petproject.resources.camera_publish
import com.ladamengh.petproject.resources.camera_retake_photo
import com.ladamengh.petproject.resources.camera_save_error
import com.ladamengh.petproject.resources.camera_take_photo
import com.ladamengh.petproject.resources.camera_title
import com.ladamengh.petproject.ui.theme.CatlasColors
import org.jetbrains.compose.resources.StringResource
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
        if (state.photo == null) {
            PhotoPickerContent(state, isCameraLoading, onTakePhoto, onChoosePhoto)
        } else {
            CatSubmissionForm(state, onIntent, onTakePhoto)
        }
    }
}

@Composable
private fun PhotoPickerContent(
    state: CameraState,
    isCameraLoading: Boolean,
    onTakePhoto: () -> Unit,
    onChoosePhoto: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(360.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
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
    state.error?.let { CameraError(it) }
    Button(
        onClick = onTakePhoto,
        enabled = !isCameraLoading,
        modifier = Modifier.fillMaxWidth(),
    ) {
        CameraProgressOrLabel(isLoading = isCameraLoading, label = Res.string.camera_take_photo)
    }
    OutlinedButton(
        onClick = onChoosePhoto,
        enabled = !isCameraLoading,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(Res.string.camera_choose_from_gallery))
    }
}

@Composable
private fun CatSubmissionForm(
    state: CameraState,
    onIntent: (CameraIntent) -> Unit,
    onTakePhoto: () -> Unit,
) {
    AsyncImage(
        model = state.photo,
        contentDescription = stringResource(Res.string.camera_photo_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().height(300.dp),
    )
    OutlinedButton(
        onClick = {
            onIntent(CameraIntent.RetakePhoto)
            onTakePhoto()
        },
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
    CatDetailsFields(state, onIntent)
    state.error?.let { CameraError(it) }
    PublishCatButton(state.isSaving, onIntent)
}

@Composable
private fun CatDetailsFields(
    state: CameraState,
    onIntent: (CameraIntent) -> Unit,
) {
    CatTextField(state.name, Res.string.camera_cat_name_label, !state.isSaving) {
        onIntent(CameraIntent.ChangeName(it))
    }
    CatTextField(state.breed, Res.string.camera_cat_breed_label, !state.isSaving) {
        onIntent(CameraIntent.ChangeBreed(it))
    }
    CatTextField(state.city, Res.string.camera_city_label, !state.isSaving) {
        onIntent(CameraIntent.ChangeCity(it))
    }
    CatTextField(
        value = state.description,
        label = Res.string.camera_description_label,
        enabled = !state.isSaving,
        minLines = 3,
        maxLines = 5,
    ) { onIntent(CameraIntent.ChangeDescription(it)) }
}

@Composable
private fun CatTextField(
    value: String,
    label: StringResource,
    enabled: Boolean,
    minLines: Int = 1,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        singleLine = minLines == 1 && maxLines == 1,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PublishCatButton(
    isSaving: Boolean,
    onIntent: (CameraIntent) -> Unit,
) {
    val saveError = stringResource(Res.string.camera_save_error)
    Button(
        onClick = {
            onIntent(CameraIntent.Save(saveError = saveError))
        },
        enabled = !isSaving,
        modifier = Modifier.fillMaxWidth(),
    ) {
        CameraProgressOrLabel(isLoading = isSaving, label = Res.string.camera_publish)
    }
}

@Composable
private fun CameraProgressOrLabel(isLoading: Boolean, label: StringResource) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.height(20.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    } else {
        Text(stringResource(label))
    }
}

@Composable
private fun CameraError(message: String) {
    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
}
