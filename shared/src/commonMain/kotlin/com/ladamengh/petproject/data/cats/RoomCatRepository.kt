package com.ladamengh.petproject.data.cats

import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.FavoriteBreedEntity
import com.ladamengh.petproject.data.local.FavoriteCatEntity
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Uses Room for user favourites while the feed is being migrated from mock data to TheCatAPI. */
class RoomCatRepository(
    database: CatlasDatabase,
) : CatRepository {
    private val dao = database.dao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mockRepository = MockCatRepository()

    override val cats = mockRepository.cats

    override val favoriteCatIds: StateFlow<Set<Int>> = dao.observeFavoriteCats()
        .map { savedCats -> savedCats.map(FavoriteCatEntity::catId).toSet() }
        .stateIn(scope, SharingStarted.Eagerly, emptySet())

    override val favoriteBreeds: StateFlow<Set<String>> = dao.observeFavoriteBreeds()
        .map { it.toSet() }
        .stateIn(scope, SharingStarted.Eagerly, emptySet())

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
