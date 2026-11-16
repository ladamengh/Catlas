package com.ladamengh.petproject.data

import android.content.Context
import androidx.room.Room
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ladamengh.petproject.data.local.CatlasDatabase
import com.ladamengh.petproject.data.local.CatEntity
import com.ladamengh.petproject.data.local.FavoriteBreedEntity
import com.ladamengh.petproject.data.local.FavoriteCatEntity
import com.ladamengh.petproject.data.local.LocalProfileEntity
import com.ladamengh.petproject.data.user.RoomProfileRepository
import com.ladamengh.petproject.feature.profile.ProfileIntent
import com.ladamengh.petproject.feature.profile.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatlasDatabaseIntegrationTest {
    private lateinit var database: CatlasDatabase

    @Before
    fun createInMemoryDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CatlasDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun feedAndFavoritesPersistAndEmitOrderedUpdates() = runBlocking {
        // Arrange
        val dao = database.dao()

        // Act
        dao.saveCats(
            listOf(
                CatEntity(2, "Мурка", "Сиамская", "Казань", "Любопытная", "cat-2.jpg"),
                CatEntity(1, "Барсик", "Мейн-кун", "Москва", "Спокойный", "cat-1.jpg"),
            ),
        )
        dao.saveFavoriteCat(FavoriteCatEntity(2, "Мурка", "Сиамская", "Казань", "Любопытная"))
        dao.saveFavoriteBreed(FavoriteBreedEntity("Сиамская"))

        // Assert
        assertEquals(listOf(2, 1), dao.observeCats().first().map { it.id })
        assertEquals(listOf(2), dao.observeFavoriteCats().first().map { it.catId })
        assertEquals(listOf("Сиамская"), dao.observeFavoriteBreeds().first())

        // Act
        dao.removeFavoriteCat(2)

        // Assert
        assertEquals(emptyList<Int>(), dao.observeFavoriteCats().first().map { it.catId })
    }

    @Test
    fun profilePersistsNullableFieldsAndAvatarBytes() = runBlocking {
        // Arrange
        val dao = database.dao()
        val avatar = byteArrayOf(0, 1, -1)

        // Act
        dao.createProfileIfMissing(LocalProfileEntity(localId = "local-1", displayName = "Анна"))
        dao.saveProfile(
            LocalProfileEntity(
                localId = "local-1",
                displayName = "Анна",
                description = null,
                city = "Казань",
                avatarImage = avatar,
            ),
        )

        val profile = requireNotNull(dao.observeProfile().first())

        // Assert
        assertEquals("Анна", profile.displayName)
        assertEquals(null, profile.description)
        assertEquals("Казань", profile.city)
        assertNotNull(profile.avatarImage)
        assertEquals(avatar.toList(), requireNotNull(profile.avatarImage).toList())
    }

    @Test
    fun profileEditFlowsThroughViewModelRepositoryAndRoom() = runBlocking {
        // Arrange
        val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        val repository = RoomProfileRepository(database, repositoryScope)
        val viewModel = ProfileViewModel(repository)
        val viewModelStore = ViewModelStore().apply { put("profile", viewModel) }

        try {
            // Act
            withContext(Dispatchers.Main.immediate) {
                viewModel.onIntent(ProfileIntent.EditOrSave)
                viewModel.onIntent(ProfileIntent.ChangeName("  Анна  "))
                viewModel.onIntent(ProfileIntent.ChangeDescription("  Люблю котов  "))
                viewModel.onIntent(ProfileIntent.ChangeCity("  Казань  "))
                viewModel.onIntent(ProfileIntent.EditOrSave)
            }

            val savedProfile = withTimeout(5_000) {
                database.dao().observeProfile().first { it?.displayName == "Анна" }
            }

            // Assert
            assertEquals("Люблю котов", savedProfile?.description)
            assertEquals("Казань", savedProfile?.city)

            withTimeout(5_000) {
                viewModel.uiState.first { it.profile.displayName == "Анна" }
            }
            assertEquals("Казань", viewModel.uiState.value.profile.city)
        } finally {
            viewModelStore.clear()
            repositoryScope.cancel()
        }
    }
}
