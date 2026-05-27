package com.openclassrooms.hexagonal.games.domain.util

sealed interface AppState {
    data object Loading : AppState
    data object Ready : AppState
    data object NotAuthenticated : AppState
    data object Offline : AppState
    data object Error : AppState
}
