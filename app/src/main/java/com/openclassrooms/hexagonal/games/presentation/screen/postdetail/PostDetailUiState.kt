package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

/**
 * UI State for the Post Detail screen.
 */
data class PostDetailUiState(
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val deleteSuccess: Boolean = false,
    val currentUserId: String? = null,
    val showErrorDialog: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val commentText: String = ""
)
