package com.ladamengh.petproject.app

import com.ladamengh.petproject.domain.model.Cat

data class CatlasUiState(
    val cats: List<Cat> = emptyList(),
    val favoriteCatIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
)
