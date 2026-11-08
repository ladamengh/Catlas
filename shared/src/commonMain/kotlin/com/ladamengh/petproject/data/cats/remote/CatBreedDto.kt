package com.ladamengh.petproject.data.cats.remote

import com.ladamengh.petproject.domain.model.CatBreed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatBreedDto(
    val id: String,
    val name: String,
    val origin: String? = null,
    val temperament: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
)

fun CatBreedDto.toDomain(): CatBreed = CatBreed(
    id = id,
    name = name,
    origin = origin.orEmpty(),
    temperament = temperament.orEmpty(),
    description = description.orEmpty(),
    imageUrl = imageUrl,
)
