@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.home

import com.ladamengh.petproject.domain.model.Cat
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

class CatDetailsViewModelTest {
    private val catRepository = mock<CatRepository>()

    @Test
    fun uiStateReflectsCatAndFavoriteUpdates() = runViewModelTest {
        // Arrange
        val selectedCat = Cat(5, "Барсик", "Мейн-кун", "Москва", "Спокойный")
        val cats = MutableStateFlow(listOf(selectedCat))
        val favoriteCatIds = MutableStateFlow(setOf(selectedCat.id))
        val favoriteBreeds = MutableStateFlow(setOf(selectedCat.breed))
        whenever(catRepository.cats).thenReturn(cats)
        whenever(catRepository.favoriteCatIds).thenReturn(favoriteCatIds)
        whenever(catRepository.favoriteBreeds).thenReturn(favoriteBreeds)
        val viewModel = CatDetailsViewModel(selectedCat.id, catRepository)

        // Act
        runCurrent()
        val initialState = viewModel.uiState.value
        favoriteCatIds.value = emptySet()
        favoriteBreeds.value = emptySet()
        runCurrent()

        // Assert
        assertEquals(selectedCat, initialState.cat)
        assertTrue(initialState.isFavorite)
        assertTrue(initialState.isBreedFavorite)
        assertFalse(viewModel.uiState.value.isFavorite)
        assertFalse(viewModel.uiState.value.isBreedFavorite)
    }

    @Test
    fun favoriteIntentsUseLoadedCatAndItsBreed() = runViewModelTest {
        // Arrange
        val selectedCat = Cat(5, "Барсик", "Мейн-кун", "Москва", "Спокойный")
        whenever(catRepository.cats).thenReturn(MutableStateFlow(listOf(selectedCat)))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        val viewModel = CatDetailsViewModel(selectedCat.id, catRepository)
        runCurrent()

        // Act
        viewModel.onIntent(CatDetailsIntent.ToggleFavorite)
        viewModel.onIntent(CatDetailsIntent.ToggleBreedFavorite)

        // Assert
        verify(catRepository).toggleFavoriteCat(selectedCat.id)
        verify(catRepository).toggleFavoriteBreed(selectedCat.breed)
    }

    @Test
    fun favoriteIntentsAreIgnoredUntilCatIsAvailable() = runViewModelTest {
        // Arrange
        whenever(catRepository.cats).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        val viewModel = CatDetailsViewModel(404, catRepository)
        runCurrent()

        // Act
        viewModel.onIntent(CatDetailsIntent.ToggleFavorite)
        viewModel.onIntent(CatDetailsIntent.ToggleBreedFavorite)

        // Assert
        verify(catRepository, never()).toggleFavoriteCat(any())
        verify(catRepository, never()).toggleFavoriteBreed(any())
    }
}

private fun runViewModelTest(block: suspend TestScope.() -> Unit) = runTest {
    Dispatchers.setMain(StandardTestDispatcher(testScheduler))
    try {
        block()
    } finally {
        Dispatchers.resetMain()
    }
}
