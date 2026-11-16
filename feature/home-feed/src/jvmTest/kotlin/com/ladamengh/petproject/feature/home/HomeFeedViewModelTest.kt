@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.home

import com.ladamengh.petproject.domain.model.Cat
import com.ladamengh.petproject.domain.repository.CatRepository
import com.ladamengh.petproject.domain.repository.CatRepositoryException
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.first
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeFeedViewModelTest {
    private val catRepository = mock<CatRepository>()

    @Test
    fun refreshFailureShowsErrorAndStopsLoading() = runViewModelTest {
        // Arrange
        whenever(catRepository.cats).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.breeds).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.refreshCats()).thenThrow(CatRepositoryException.RequestFailed(IOException("offline")))
        val viewModel = HomeFeedViewModel(catRepository)

        // Act
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value.hasError)
        assertFalse(viewModel.uiState.value.isLoading)
        verify(catRepository, times(1)).refreshCats()
    }

    @Test
    fun retryAfterFailureLoadsCatsAndClearsError() = runViewModelTest {
        // Arrange
        val catsFlow = MutableStateFlow<List<Cat>>(emptyList())
        whenever(catRepository.cats).thenReturn(catsFlow)
        whenever(catRepository.breeds).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.refreshCats())
            .thenThrow(CatRepositoryException.RequestFailed(IOException("offline")))
            .thenReturn(Unit)
        val viewModel = HomeFeedViewModel(catRepository)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.hasError)

        // Act
        catsFlow.value = listOf(Cat(7, "Мурка", "Сиамская", "Казань", "Любопытная"))
        viewModel.onIntent(HomeFeedIntent.Retry)
        advanceUntilIdle()

        // Assert
        assertEquals("Мурка", viewModel.uiState.value.cats.single().name)
        assertFalse(viewModel.uiState.value.hasError)
        assertFalse(viewModel.uiState.value.isLoading)
        verify(catRepository, times(2)).refreshCats()
    }

    @Test
    fun openCatEmitsNavigationEffect() = runViewModelTest {
        // Arrange
        whenever(catRepository.cats).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.breeds).thenReturn(MutableStateFlow(emptyList()))
        whenever(catRepository.favoriteCatIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.favoriteBreeds).thenReturn(MutableStateFlow(emptySet()))
        whenever(catRepository.refreshCats()).thenReturn(Unit)
        val viewModel = HomeFeedViewModel(catRepository)
        val effect = async { viewModel.effects.first() }
        runCurrent()

        // Act
        viewModel.onIntent(HomeFeedIntent.OpenCat(12))
        advanceUntilIdle()

        // Assert
        assertEquals(HomeFeedEffect.NavigateToCat(12), effect.await())
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
