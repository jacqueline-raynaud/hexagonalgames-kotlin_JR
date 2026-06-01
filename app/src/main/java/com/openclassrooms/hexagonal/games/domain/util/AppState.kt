package com.openclassrooms.hexagonal.games.domain.util

sealed interface AppState {
    object Loading : AppState
    object Ready : AppState
    object NotAuthenticated : AppState
    object Offline : AppState
    object Error : AppState
}
