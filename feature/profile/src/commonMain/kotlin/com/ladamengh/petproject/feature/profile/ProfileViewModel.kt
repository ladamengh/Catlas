package com.ladamengh.petproject.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.model.UserProfile
import com.ladamengh.petproject.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<ProfileEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            profileRepository.profile.collect { profile ->
                _uiState.update { state ->
                    if (state.isEditing) {
                        state.copy(profile = profile)
                    } else {
                        state.copy(
                            profile = profile,
                            draftName = profile.displayName,
                            draftDescription = profile.description.orEmpty(),
                            draftCity = profile.city.orEmpty(),
                            draftAvatarImage = profile.avatarImage,
                        )
                    }
                }
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.EditOrSave -> editOrSave()
            is ProfileIntent.ChangeName -> _uiState.update { it.copy(draftName = intent.value) }
            is ProfileIntent.ChangeDescription ->
                _uiState.update { it.copy(draftDescription = intent.value) }
            is ProfileIntent.ChangeCity -> _uiState.update { it.copy(draftCity = intent.value) }

            ProfileIntent.PickAvatar -> effectChannel.trySend(ProfileEffect.OpenAvatarPicker)
            is ProfileIntent.AvatarSelected ->
                _uiState.update { it.copy(draftAvatarImage = intent.image) }
        }
    }

    private fun editOrSave() {
        val state = _uiState.value
        if (!state.isEditing) {
            _uiState.update {
                it.copy(
                    isEditing = true,
                    draftName = it.profile.displayName,
                    draftDescription = it.profile.description.orEmpty(),
                    draftCity = it.profile.city.orEmpty(),
                    draftAvatarImage = it.profile.avatarImage,
                )
            }
            return
        }

        val name = state.draftName.trim()
        if (name.isBlank()) return
        val description = state.draftDescription.trim().takeIf(String::isNotEmpty)
        val city = state.draftCity.trim().takeIf(String::isNotEmpty)
        profileRepository.saveProfile(name, description, city, state.draftAvatarImage)
        _uiState.update {
            it.copy(
                profile = UserProfile(
                    id = it.profile.id,
                    displayName = name,
                    description = description,
                    city = city,
                    avatarImage = it.draftAvatarImage,
                ),
                isEditing = false,
            )
        }
    }
}
