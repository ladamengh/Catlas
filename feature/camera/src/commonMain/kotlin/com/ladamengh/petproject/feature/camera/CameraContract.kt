package com.ladamengh.petproject.feature.camera

data class CameraState(
    val photo: ByteArray? = null,
    val name: String = "",
    val breed: String = "",
    val city: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
)

sealed interface CameraIntent {
    data class PhotoCaptured(val bytes: ByteArray) : CameraIntent
    data object RetakePhoto : CameraIntent
    data class ChangeName(val value: String) : CameraIntent
    data class ChangeBreed(val value: String) : CameraIntent
    data class ChangeCity(val value: String) : CameraIntent
    data class ChangeDescription(val value: String) : CameraIntent
    data class Save(val saveError: String) : CameraIntent
    data class ShowError(val message: String) : CameraIntent
}

sealed interface CameraEffect {
    data object CatPublished : CameraEffect
}
