package com.openclassrooms.hexagonal.games.presentation.screen.homefeed

import com.openclassrooms.hexagonal.games.domain.util.AppState

/**
 * UI State for the Homefeed screen.
 */
data class HomefeedUiState(
    val posts: List<PostUi> = emptyList(),
    val currentUserName: String? = null,
    val showMenu: Boolean = false,
    val showError: Boolean = false,
    val appState: AppState = AppState.NotAuthenticated
)
