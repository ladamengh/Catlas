@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.camera

import com.ladamengh.petproject.domain.repository.CatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class CameraViewModelTest {
    private val catRepository = mock<CatRepository>()

    @Test
    fun saveWithoutPhotoDoesNotPublish() = runViewModelTest {
        // Arrange
        val viewModel = CameraViewModel(catRepository)
        viewModel.onIntent(CameraIntent.ChangeName("Мурка"))
        viewModel.onIntent(CameraIntent.ChangeBreed("Сиамская"))

        // Act
        viewModel.onIntent(CameraIntent.Save("Заполните данные", "Не удалось сохранить"))
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.uiState.value.isSaving)
        assertNull(viewModel.uiState.value.error)
        verify(catRepository, never()).publishCat(any(), any(), any(), any(), any())
    }

    @Test
    fun saveWithMissingRequiredFieldsShowsValidationError() = runViewModelTest {
        // Arrange
        val viewModel = CameraViewModel(catRepository)
        viewModel.onIntent(CameraIntent.PhotoCaptured(byteArrayOf(1, 2, 3)))
        viewModel.onIntent(CameraIntent.ChangeName("   "))
        viewModel.onIntent(CameraIntent.ChangeBreed("Сиамская"))

        // Act
        viewModel.onIntent(CameraIntent.Save("Укажите имя и породу", "Ошибка сохранения"))
        advanceUntilIdle()

        // Assert
        assertEquals("Укажите имя и породу", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaving)
        verify(catRepository, never()).publishCat(any(), any(), any(), any(), any())
    }

    @Test
    fun successfulPublishTrimsOptionalFieldsResetsFormAndEmitsEffect() = runViewModelTest {
        // Arrange
        whenever(catRepository.publishCat(any(), any(), any(), any(), any())).thenReturn(Unit)
        val imageBytes = byteArrayOf(4, 5, 6)
        val viewModel = CameraViewModel(catRepository)
        val effect = async { viewModel.effects.first() }
        runCurrent()

        // Act
        viewModel.onIntent(CameraIntent.PhotoCaptured(imageBytes))
        viewModel.onIntent(CameraIntent.ChangeName("Мурка"))
        viewModel.onIntent(CameraIntent.ChangeBreed("Сиамская"))
        viewModel.onIntent(CameraIntent.ChangeCity("  Казань  "))
        viewModel.onIntent(CameraIntent.ChangeDescription("  Спокойная  "))
        viewModel.onIntent(CameraIntent.Save("Заполните поля", "Ошибка"))
        advanceUntilIdle()

        // Assert
        val capturedImageBytes = argumentCaptor<ByteArray>()
        verify(catRepository).publishCat(
            eq("Мурка"),
            eq("Сиамская"),
            eq("Казань"),
            eq("Спокойная"),
            capturedImageBytes.capture(),
        )
        assertContentEquals(imageBytes, capturedImageBytes.firstValue)
        assertEquals(CameraEffect.CatPublished, effect.await())
        assertNull(viewModel.uiState.value.photo)
        assertEquals("", viewModel.uiState.value.name)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun publishFailureKeepsDraftAndShowsRepositoryMessage() = runViewModelTest {
        // Arrange
        whenever(catRepository.publishCat(any(), any(), any(), any(), any()))
            .thenThrow(IllegalStateException("Сеть недоступна"))
        val viewModel = CameraViewModel(catRepository)
        val imageBytes = byteArrayOf(7)
        viewModel.onIntent(CameraIntent.PhotoCaptured(imageBytes))
        viewModel.onIntent(CameraIntent.ChangeName("Кузя"))
        viewModel.onIntent(CameraIntent.ChangeBreed("Дворянин"))

        // Act
        viewModel.onIntent(CameraIntent.Save("Заполните поля", "Общая ошибка"))
        advanceUntilIdle()

        // Assert
        assertEquals("Сеть недоступна", viewModel.uiState.value.error)
        assertEquals("Кузя", viewModel.uiState.value.name)
        assertContentEquals(imageBytes, viewModel.uiState.value.photo)
        assertFalse(viewModel.uiState.value.isSaving)
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
