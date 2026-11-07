package com.ladamengh.petproject.domain.repository

import com.ladamengh.petproject.domain.model.Cat
import kotlinx.coroutines.flow.StateFlow

interface CatRepository {
    val cats: StateFlow<List<Cat>>
    val favoriteCatIds: StateFlow<Set<Int>>
    val favoriteBreeds: StateFlow<Set<String>>

    fun toggleFavoriteCat(catId: Int)
    fun toggleFavoriteBreed(breed: String)
}
