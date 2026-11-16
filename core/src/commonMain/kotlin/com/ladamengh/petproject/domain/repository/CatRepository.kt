package com.ladamengh.petproject.domain.repository

import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.model.CatBreed
import kotlinx.coroutines.flow.StateFlow

sealed class CatRepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class RequestFailed(cause: Throwable) :
        CatRepositoryException(cause.message ?: "The cat service request failed.", cause)

    class NoBreedsAvailable : CatRepositoryException("The cat service returned no breeds.")
}

interface CatRepository {
    val cats: StateFlow<List<Cat>>
    val breeds: StateFlow<List<CatBreed>>
    val favoriteCatIds: StateFlow<Set<Int>>
    val favoriteBreeds: StateFlow<Set<String>>

    suspend fun refreshBreeds()
    suspend fun refreshCats()
    suspend fun publishCat(
        name: String,
        breed: String,
        location: String,
        description: String,
        image: ByteArray,
    )

    fun toggleFavoriteCat(catId: Int)
    fun toggleFavoriteBreed(breed: String)
}
