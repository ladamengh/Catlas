package com.ladamengh.petproject.feature.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.extensions.loadBytes
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.rememberImagePickerKMP
import catlas.shared.generated.resources.Res
import catlas.shared.generated.resources.camera_open_error
import catlas.shared.generated.resources.camera_photo_read_error
import kotlinx.coroutines.flow.collect
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CameraRoute(
    onPublished: () -> Unit,
) {
    val photoReadError = stringResource(Res.string.camera_photo_read_error)
    val cameraOpenError = stringResource(Res.string.camera_open_error)
    val viewModel: CameraViewModel = koinViewModel(key = "camera")
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH,
                redactGpsData = true,
            ),
        ),
    )

    LaunchedEffect(viewModel, onPublished) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CameraEffect.CatPublished -> onPublished()
            }
        }
    }

    LaunchedEffect(picker.result) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                result.first?.loadBytes()?.let { bytes ->
                    viewModel.onIntent(CameraIntent.PhotoCaptured(bytes))
                } ?: viewModel.onIntent(CameraIntent.ShowError(photoReadError))
                picker.reset()
            }
            is ImagePickerResult.Error -> {
                viewModel.onIntent(
                    CameraIntent.ShowError(result.exception.message ?: cameraOpenError),
                )
                picker.reset()
            }
            else -> Unit
        }
    }

    CameraScreen(
        state = state,
        isCameraLoading = picker.result is ImagePickerResult.Loading,
        onIntent = viewModel::onIntent,
        onTakePhoto = { picker.launchCamera() },
        onChoosePhoto = { picker.launchGallery() },
    )
}
