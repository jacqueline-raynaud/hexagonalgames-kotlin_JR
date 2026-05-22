package com.openclassrooms.hexagonal.games.util

sealed class AppState {
    object Ready : AppState()
    object NotAuthenticated : AppState()
    object Offline : AppState()
}