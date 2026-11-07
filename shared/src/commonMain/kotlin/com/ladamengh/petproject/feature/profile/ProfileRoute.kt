package com.ladamengh.petproject.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ladamengh.petproject.domain.repository.ProfileRepository
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.extensions.loadBytes
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.rememberImagePickerKMP
import kotlinx.coroutines.flow.collect

@Composable
fun ProfileRoute(profileRepository: ProfileRepository) {
    val viewModel: ProfileViewModel = viewModel(key = "profile") {
        ProfileViewModel(profileRepository)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = false,
                selectionLimit = 1,
                compressionLevel = CompressionLevel.HIGH,
                redactGpsData = true,
            ),
            cropConfig = CropConfig(
                enabled = true,
                squareCrop = true,
                circularCrop = true,
                freeformCrop = false,
            ),
        ),
    )

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProfileEffect.OpenAvatarPicker -> picker.launchGallery()
            }
        }
    }

    val pickerResult = picker.result
    LaunchedEffect(pickerResult) {
        if (pickerResult is ImagePickerResult.Success) {
            pickerResult.first?.loadBytes()?.let { image ->
                viewModel.onIntent(ProfileIntent.AvatarSelected(image))
            }
            picker.reset()
        }
    }

    ProfileScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
