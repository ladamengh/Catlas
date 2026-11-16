package com.ladamengh.petproject.data.cats.remote

import com.ladamengh.petproject.domain.model.Cat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatDto(
    val id: Int,
    val name: String? = null,
    val breed: String? = null,
    val location: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String,
)

@Serializable
data class CreateCatDto(
    val name: String? = null,
    val breed: String? = null,
    val location: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String,
)

fun CatDto.toDomain() = Cat(
    id = id,
    name = name.orEmpty(),
    breed = breed.orEmpty(),
    location = location.orEmpty(),
    description = description.orEmpty(),
    imageUrl = imageUrl,
)
