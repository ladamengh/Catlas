package com.ladamengh.petproject.data.cats.remote

import com.ladamengh.petproject.domain.model.Cat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatDto(
    val id: Int,
    val name: String,
    val breed: String,
    val location: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String,
)

@Serializable
data class CreateCatDto(
    val name: String,
    val breed: String,
    val location: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String,
)

fun CatDto.toDomain() = Cat(
    id = id,
    name = name,
    breed = breed,
    location = location,
    description = description,
    imageUrl = imageUrl,
)
