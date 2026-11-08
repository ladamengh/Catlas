package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.FavoriteBreedEntity
import com.ladamengh.petproject.data.local.FavoriteCatEntity
import com.ladamengh.petproject.domain.model.CatBreed
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
    private val mockRepository = MockCatRepository()

    override val cats = mockRepository.cats
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

    override fun toggleFavoriteCat(catId: Int) {
        scope.launch {
            if (dao.getFavoriteCat(catId) != null) {
                dao.removeFavoriteCat(catId)
            } else {
                mockRepository.cats.value.firstOrNull { it.id == catId }?.let { cat ->
                    dao.saveFavoriteCat(
                        FavoriteCatEntity(
                            catId = cat.id,
                            name = cat.name,
                            breed = cat.breed,
                            location = cat.location,
                            description = cat.description,
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
