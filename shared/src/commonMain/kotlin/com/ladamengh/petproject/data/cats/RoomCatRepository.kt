package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.CatEntity
import com.ladamengh.petproject.data.local.FavoriteBreedEntity
import com.ladamengh.petproject.data.local.FavoriteCatEntity
import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Uses Supabase for breeds and Room for favourites. */
class RoomCatRepository(
    database: CatlasDatabase,
    private val remoteDataSource: SupabaseCatDataSource,
) : CatRepository {
    private val dao = database.dao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val cats: StateFlow<List<Cat>> = dao.observeCats()
        .map { entities -> entities.map(CatEntity::toDomain) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    private val _breeds = MutableStateFlow(emptyList<CatBreed>())
    override val breeds = _breeds

    override val favoriteCatIds: StateFlow<Set<Int>> = dao.observeFavoriteCats()
        .map { savedCats -> savedCats.map(FavoriteCatEntity::catId).toSet() }
        .stateIn(scope, SharingStarted.Eagerly, emptySet())

    override val favoriteBreeds: StateFlow<Set<String>> = dao.observeFavoriteBreeds()
        .map { it.toSet() }
        .stateIn(scope, SharingStarted.Eagerly, emptySet())

    override suspend fun refreshBreeds() {
        val breeds = remoteDataSource.fetchBreeds()
        if (breeds.isEmpty()) error("Supabase returned no breeds.")
        _breeds.value = breeds
    }

    override suspend fun refreshCats() {
        dao.saveCats(remoteDataSource.fetchCats().map { cat ->
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
        val cat = remoteDataSource.publishCat(name, breed, location, description, image)
        dao.saveCat(
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
            if (dao.getFavoriteCat(catId) != null) {
                dao.removeFavoriteCat(catId)
            } else {
                cats.value.firstOrNull { it.id == catId }?.let { cat ->
                    dao.saveFavoriteCat(
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
            if (dao.getFavoriteBreed(breed) != null) {
                dao.removeFavoriteBreed(breed)
            } else {
                dao.saveFavoriteBreed(FavoriteBreedEntity(breed))
            }
        }
    }
}

private fun CatEntity.toDomain() = Cat(
    id = id,
    name = name,
    breed = breed,
    location = location,
    description = description,
    imageUrl = imageUrl,
)
