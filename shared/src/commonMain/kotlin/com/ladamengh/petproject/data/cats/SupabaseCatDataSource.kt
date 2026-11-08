package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.data.cats.remote.SupabaseBreedsService
import com.ladamengh.petproject.data.cats.remote.toDomain
import com.ladamengh.petproject.domain.model.CatBreed

class SupabaseCatDataSource(
    private val service: SupabaseBreedsService,
) {
    suspend fun fetchBreeds(): List<CatBreed> = service.getBreeds().map { it.toDomain() }
}
