package com.ladamengh.petproject.domain.model

data class UserProfile(
    val id: String,
    val displayName: String,
    val description: String?,
    val city: String? = null,
    val avatarImage: ByteArray?,
)
