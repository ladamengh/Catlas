@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.favorites

import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FavoritesViewModelTest {
    private val catRepository = mock<CatRepository>()

    @Test
    fun uiStateTracksFavoriteDataAndSelectedSection() = runViewModelTest {
        // Arrange
        val cats = MutableStateFlow(listOf(Cat(4, "Мурка", "Сиамская", "Казань", "Любопытная")))
        val favoriteCatIds = MutableStateFlow(setOf(4))
        val favoriteBreeds = MutableStateFlow(setOf("Сиамская"))
        val breeds = MutableStateFlow(listOf(breed(id = "siamese", name = "Сиамская")))
        stubCatRepository(cats, favoriteCatIds, favoriteBreeds, breeds)
        val viewModel = FavoritesViewModel(catRepository)
        runCurrent()

        // Act
        favoriteCatIds.value = emptySet()
        viewModel.onIntent(FavoritesIntent.SelectSection(FavoritesSection.Breeds))
        runCurrent()

        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Мурка", viewModel.uiState.value.cats.single().name)
        assertEquals(emptySet(), viewModel.uiState.value.favoriteCatIds)
        assertEquals(setOf("Сиамская"), viewModel.uiState.value.favoriteBreeds)
        assertEquals(FavoritesSection.Breeds, viewModel.uiState.value.selectedSection)
    }

    @Test
    fun favoriteActionsAreDelegatedToRepository() = runViewModelTest {
        // Arrange
        stubCatRepository()
        val viewModel = FavoritesViewModel(catRepository)

        // Act
        viewModel.onIntent(FavoritesIntent.ToggleFavoriteCat(17))
        viewModel.onIntent(FavoritesIntent.ToggleFavoriteBreed("Британская"))

        // Assert
        verify(catRepository).toggleFavoriteCat(17)
        verify(catRepository).toggleFavoriteBreed("Британская")
    }

    @Test
    fun openCatAndKnownBreedEmitNavigationEffects() = runViewModelTest {
        // Arrange
        stubCatRepository(
            breeds = MutableStateFlow(listOf(breed(id = "british", name = "Британская"))),
        )
        val viewModel = FavoritesViewModel(catRepository)
        val effects = async { viewModel.effects.take(2).toList() }
        runCurrent()

        // Act
        viewModel.onIntent(FavoritesIntent.OpenCat(9))
        viewModel.onIntent(FavoritesIntent.OpenBreed("Британская"))
        advanceUntilIdle()

        // Assert
        assertEquals(
            listOf(
                FavoritesEffect.NavigateToCat(9),
                FavoritesEffect.NavigateToBreed("british"),
            ),
            effects.await(),
        )
    }

    private fun stubCatRepository(
        cats: MutableStateFlow<List<Cat>> = MutableStateFlow(emptyList()),
        favoriteCatIds: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet()),
        favoriteBreeds: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet()),
        breeds: MutableStateFlow<List<CatBreed>> = MutableStateFlow(emptyList()),
    ) {
        whenever(catRepository.cats).thenReturn(cats)
        whenever(catRepository.favoriteCatIds).thenReturn(favoriteCatIds)
        whenever(catRepository.favoriteBreeds).thenReturn(favoriteBreeds)
        whenever(catRepository.breeds).thenReturn(breeds)
    }
}

private fun breed(id: String, name: String) = CatBreed(
    id = id,
    name = name,
    origin = "",
    temperament = "",
    description = "",
)

private fun runViewModelTest(block: suspend TestScope.() -> Unit) = runTest {
    Dispatchers.setMain(StandardTestDispatcher(testScheduler))
    try {
        block()
    } finally {
        Dispatchers.resetMain()
    }
}
