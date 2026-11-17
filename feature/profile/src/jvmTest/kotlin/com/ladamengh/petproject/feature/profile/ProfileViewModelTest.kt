@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.ladamengh.petproject.feature.profile

import com.ladamengh.petproject.domain.model.UserProfile
import com.ladamengh.petproject.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
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

class ProfileViewModelTest {
    private val profileRepository = mock<ProfileRepository>()

    @Test
    fun savesTrimmedProfileFields() = runViewModelTest {
        // Arrange
        whenever(profileRepository.profile).thenReturn(
            MutableStateFlow(
                UserProfile(id = "local", displayName = "Любитель котиков", description = null, avatarImage = null),
            ),
        )
        val viewModel = ProfileViewModel(profileRepository)
        advanceUntilIdle()

        // Act
        viewModel.onIntent(ProfileIntent.EditOrSave)
        viewModel.onIntent(ProfileIntent.ChangeName("  Анна  "))
        viewModel.onIntent(ProfileIntent.ChangeDescription("  Люблю котов  "))
        viewModel.onIntent(ProfileIntent.ChangeCity("  Казань "))
        viewModel.onIntent(ProfileIntent.EditOrSave)

        // Assert
        verify(profileRepository).saveProfile("Анна", "Люблю котов", "Казань", null)
        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun blankNameDoesNotSaveAndKeepsEditing() = runViewModelTest {
        // Arrange
        whenever(profileRepository.profile).thenReturn(
            MutableStateFlow(
                UserProfile(id = "local", displayName = "Любитель котиков", description = null, avatarImage = null),
            ),
        )
        val viewModel = ProfileViewModel(profileRepository)
        advanceUntilIdle()

        // Act
        viewModel.onIntent(ProfileIntent.EditOrSave)
        viewModel.onIntent(ProfileIntent.ChangeName("   "))
        viewModel.onIntent(ProfileIntent.EditOrSave)

        // Assert
        assertEquals("Любитель котиков", viewModel.uiState.value.profile.displayName)
        assertTrue(viewModel.uiState.value.isEditing)
        verify(profileRepository, never()).saveProfile(any(), any(), any(), any())
    }
}

private fun runViewModelTest(block: suspend TestScope.() -> Unit) {
    val mainDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(mainDispatcher)
    try {
        runTest(mainDispatcher) { block() }
    } finally {
        Dispatchers.resetMain()
    }
}
