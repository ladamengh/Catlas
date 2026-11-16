package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.CatEntity
import com.ladamengh.petproject.data.local.FavoriteBreedEntity
import com.ladamengh.petproject.data.local.FavoriteCatEntity
import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.repository.CatRepository
import com.ladamengh.petproject.domain.repository.CatRepositoryException
import io.ktor.client.plugins.ResponseException
import kotlinx.serialization.SerializationException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.io.IOException

/** Uses Supabase for breeds and Room for favourites. */
class RoomCatRepository(
    database: CatlasDatabase,
    private val remoteDataSource: SupabaseCatDataSource,
) : CatRepository {
    private val catDao = database.catDao()
    private val favoriteCatDao = database.favoriteCatDao()
    private val favoriteBreedDao = database.favoriteBreedDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val cats: StateFlow<List<Cat>> = catDao.observeCats()
        .map { entities -> entities.map(CatEntity::toDomain) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    private val _breeds = MutableStateFlow(emptyList<CatBreed>())
    override val breeds = _breeds

    override val favoriteCatIds: StateFlow<Set<Int>> = favoriteCatDao.observeFavoriteCats()
        .map { savedCats -> savedCats.map(FavoriteCatEntity::catId).toSet() }
        .stateIn(scope, SharingStarted.Eagerly, emptySet())

    override val favoriteBreeds: StateFlow<Set<String>> = favoriteBreedDao.observeFavoriteBreeds()
        .map { it.toSet() }
        .stateIn(scope, SharingStarted.Eagerly, emptySet())

    override suspend fun refreshBreeds() {
        val breeds = requestFromService { remoteDataSource.fetchBreeds() }
        if (breeds.isEmpty()) throw CatRepositoryException.NoBreedsAvailable()
        _breeds.value = breeds
    }

    override suspend fun refreshCats() {
        val cats = requestFromService { remoteDataSource.fetchCats() }
        catDao.saveCats(cats.map { cat ->
            CatEntity(
                id = cat.id,
                name = cat.name,
                breed = cat.breed,
                location = cat.location,
                description = cat.description,
                imageUrl = cat.imageUrl.orEmpty(),
            )
        })
    }

    override suspend fun publishCat(
        name: String,
        breed: String,
        location: String,
        description: String,
        image: ByteArray,
    ) {
        val cat = requestFromService {
            remoteDataSource.publishCat(name, breed, location, description, image)
        }
        catDao.saveCat(
            CatEntity(
                id = cat.id,
                name = cat.name,
                breed = cat.breed,
                location = cat.location,
                description = cat.description,
                imageUrl = cat.imageUrl.orEmpty(),
            ),
        )
    }

    override fun toggleFavoriteCat(catId: Int) {
        scope.launch {
            if (favoriteCatDao.getFavoriteCat(catId) != null) {
                favoriteCatDao.removeFavoriteCat(catId)
            } else {
                cats.value.firstOrNull { it.id == catId }?.let { cat ->
                    favoriteCatDao.saveFavoriteCat(
                        FavoriteCatEntity(
                            catId = cat.id,
                            name = cat.name,
                            breed = cat.breed,
                            location = cat.location,
                            description = cat.description,
                            imageUrl = cat.imageUrl,
                        ),
                    )
                }
            }
        }
    }

    override fun toggleFavoriteBreed(breed: String) {
        scope.launch {
            if (favoriteBreedDao.getFavoriteBreed(breed) != null) {
                favoriteBreedDao.removeFavoriteBreed(breed)
            } else {
                favoriteBreedDao.saveFavoriteBreed(FavoriteBreedEntity(breed))
            }
        }
    }

    fun close() {
        scope.cancel()
    }
}

private suspend inline fun <T> requestFromService(crossinline request: suspend () -> T): T = try {
    request()
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (exception: IOException) {
    throw CatRepositoryException.RequestFailed(exception)
} catch (exception: ResponseException) {
    throw CatRepositoryException.RequestFailed(exception)
} catch (exception: SerializationException) {
    throw CatRepositoryException.RequestFailed(exception)
}

private fun CatEntity.toDomain() = Cat(
    id = id,
    name = name,
    breed = breed,
    location = location,
    description = description,
    imageUrl = imageUrl,
)
