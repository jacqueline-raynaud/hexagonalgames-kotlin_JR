package com.openclassrooms.hexagonal.games.domain.util

sealed interface AppState {
    data object Ready : AppState
    data object NotAuthenticated : AppState
    data object Offline : AppState
}