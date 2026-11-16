package com.ladamengh.petproject.feature.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladamengh.petproject.domain.repository.CatRepository
import com.ladamengh.petproject.domain.repository.CatRepositoryException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val catRepository: CatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CameraState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<CameraEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    fun onIntent(intent: CameraIntent) {
        when (intent) {
            is CameraIntent.PhotoCaptured -> _uiState.update {
                it.copy(photo = intent.bytes, error = null)
            }
            CameraIntent.RetakePhoto -> _uiState.value = CameraState()
            is CameraIntent.ChangeName -> _uiState.update { it.copy(name = intent.value) }
            is CameraIntent.ChangeBreed -> _uiState.update { it.copy(breed = intent.value) }
            is CameraIntent.ChangeCity -> _uiState.update { it.copy(city = intent.value) }
            is CameraIntent.ChangeDescription -> _uiState.update { it.copy(description = intent.value) }
            is CameraIntent.ShowError -> _uiState.update { it.copy(error = intent.message) }
            is CameraIntent.Save -> saveCat(intent)
        }
    }

    private fun saveCat(intent: CameraIntent.Save) {
        val state = _uiState.value
        val photo = state.photo ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                catRepository.publishCat(
                    name = state.name,
                    breed = state.breed,
                    location = state.city.trim(),
                    description = state.description.trim(),
                    image = photo,
                )
                _uiState.value = CameraState()
                effectChannel.send(CameraEffect.CatPublished)
            } catch (exception: CatRepositoryException) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = exception.message ?: intent.saveError,
                    )
                }
            }
        }
    }
}
