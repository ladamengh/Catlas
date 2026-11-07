package com.ladamengh.petproject.domain.repository

import com.ladamengh.petproject.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val profile: StateFlow<UserProfile>

    fun saveProfile(
        displayName: String,
        description: String?,
        city: String?,
        avatarImage: ByteArray?,
    )
}
