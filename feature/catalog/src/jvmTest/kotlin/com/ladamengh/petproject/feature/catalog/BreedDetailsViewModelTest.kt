@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.catalog

import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BreedDetailsViewModelTest {
    private val catRepository = mock<CatRepository>()

    @Test
    fun uiStateReflectsBreedAndFavoriteUpdates() = runViewModelTest {
        // Arrange
        val selectedBreed = breed()
        val breeds = MutableStateFlow(listOf(selectedBreed))
        val favoriteBreeds = MutableStateFlow(setOf(selectedBreed.name))
        whenever(catRepository.breeds).thenReturn(breeds)
        whenever(catRepository.favoriteBreeds).thenReturn(favoriteBreeds)
        val viewModel = BreedDetailsViewModel(selectedBreed.id, catRepository)

        // Act
        runCurrent()
        val initialState = viewModel.uiState.value
        favoriteBreeds.value = emptySet()
        runCurrent()

        // Assert
        assertEquals(selectedBreed, initialState.breed)
        assertTrue(initialState.isFavorite)
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun toggleFavoriteUsesBreedNameWhenBreedIsLoaded() = runViewModelTest {
        // Arrange
        whenever(catRepository.breeds).thenReturn(MutableStateFlow(listOf(breed())))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        val viewModel = BreedDetailsViewModel("british", catRepository)
        runCurrent()

        // Act
        viewModel.onIntent(BreedDetailsIntent.ToggleFavorite)

        // Assert
        verify(catRepository).toggleFavoriteBreed("Британская")
    }

    @Test
    fun toggleFavoriteIsIgnoredWhenBreedIsMissing() = runViewModelTest {
        // Arrange
        whenever(catRepository.breeds).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        val viewModel = BreedDetailsViewModel("unknown", catRepository)
        runCurrent()

        // Act
        viewModel.onIntent(BreedDetailsIntent.ToggleFavorite)

        // Assert
        verify(catRepository, never()).toggleFavoriteBreed(any())
    }
}

private fun breed() = CatBreed(
    id = "british",
    name = "Британская",
    origin = "Великобритания",
    temperament = "Спокойная",
    description = "Короткошёрстная порода",
)

private fun runViewModelTest(block: suspend TestScope.() -> Unit) = runTest {
    Dispatchers.setMain(StandardTestDispatcher(testScheduler))
    try {
        block()
    } finally {
        Dispatchers.resetMain()
    }
}
