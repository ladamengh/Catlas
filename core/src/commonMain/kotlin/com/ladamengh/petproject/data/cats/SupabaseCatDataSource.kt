package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.data.cats.remote.SupabaseBreedsService
import com.ladamengh.petproject.data.cats.remote.SupabaseCatsService
import com.ladamengh.petproject.data.cats.remote.toDomain
import com.ladamengh.petproject.data.cats.remote.toDomain
import com.ladamengh.petproject.domain.model.CatBreed

class SupabaseCatDataSource(
    private val service: SupabaseBreedsService,
    private val catsService: SupabaseCatsService,
) {
    suspend fun fetchBreeds(): List<CatBreed> = service.getBreeds().map { it.toDomain() }

    suspend fun fetchCats() = catsService.getCats().map { it.toDomain() }

    suspend fun publishCat(
        name: String,
        breed: String,
        location: String,
        description: String,
        image: ByteArray,
    ) = catsService.createCat(name, breed, location, description, image).toDomain()
}
