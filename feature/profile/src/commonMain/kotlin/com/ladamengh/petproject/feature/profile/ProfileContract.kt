package com.ladamengh.petproject.feature.profile

import com.ladamengh.petproject.domain.model.UserProfile

data class ProfileState(
    val profile: UserProfile = UserProfile(
        id = "",
        displayName = "Любитель котиков",
        description = null,
        avatarImage = null,
    ),
    val isEditing: Boolean = false,
    val draftName: String = "Любитель котиков",
    val draftDescription: String = "",
    val draftCity: String = "",
    val draftAvatarImage: ByteArray? = null,
)

sealed interface ProfileIntent {
    data object EditOrSave : ProfileIntent
    data class ChangeName(val value: String) : ProfileIntent
    data class ChangeDescription(val value: String) : ProfileIntent
    data class ChangeCity(val value: String) : ProfileIntent
    data object PickAvatar : ProfileIntent
    data class AvatarSelected(val image: ByteArray) : ProfileIntent
}

sealed interface ProfileEffect {
    data object OpenAvatarPicker : ProfileEffect
}
