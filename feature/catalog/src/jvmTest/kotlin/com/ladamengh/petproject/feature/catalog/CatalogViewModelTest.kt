@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.catalog

import com.ladamengh.petproject.domain.model.CatBreed
import com.ladamengh.petproject.domain.repository.CatRepository
import com.ladamengh.petproject.domain.repository.CatRepositoryException
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatalogViewModelTest {
    private val catRepository = mock<CatRepository>()

    @Test
    fun breedsAreSortedByName() = runViewModelTest {
        // Arrange
        val breeds = MutableStateFlow(
            listOf(
                CatBreed(
                    id = "b",
                    name = "Сиамская",
                    origin = "Таиланд",
                    temperament = "Общительная",
                    description = "",
                ),
                CatBreed(
                    id = "a",
                    name = "Абиссинская",
                    origin = "Эфиопия",
                    temperament = "Активная",
                    description = "",
                ),
            ),
        )
        whenever(catRepository.breeds).thenReturn(breeds)
        whenever(catRepository.cats).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.refreshBreeds()).thenReturn(Unit)
        val viewModel = CatalogViewModel(catRepository)

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("Абиссинская", "Сиамская"), viewModel.uiState.value.breeds.map { it.name })
        assertFalse(viewModel.uiState.value.hasError)
    }

    @Test
    fun refreshFailureCanBeRetried() = runViewModelTest {
        // Arrange
        whenever(catRepository.breeds).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.cats).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.refreshBreeds())
            .thenThrow(CatRepositoryException.RequestFailed(IOException("service unavailable")))
            .thenReturn(Unit)
        val viewModel = CatalogViewModel(catRepository)

        // Act
        advanceUntilIdle()
        val errorState = viewModel.uiState.value
        viewModel.onIntent(CatalogIntent.Retry)
        advanceUntilIdle()

        // Assert
        assertTrue(errorState.hasError)
        assertFalse(viewModel.uiState.value.hasError)
        assertFalse(viewModel.uiState.value.isLoading)
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
