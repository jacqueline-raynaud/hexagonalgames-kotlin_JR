package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

data class PostDetailUiState (
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val deleteSuccess: Boolean = false,
    val currentUserId: String? = null
)