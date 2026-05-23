package com.openclassrooms.hexagonal.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.util.AppState
import com.openclassrooms.hexagonal.games.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.util.NetworkStateMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Base class for all ViewModels in the presentation layer.
 *
 * This abstract class serves as the foundation for managing UI-related data and logic,
 * ensuring architectural consistency across the application's different features.
 */
abstract class BaseViewModel(
    authStateMonitor: AuthStateMonitor,
    networkMonitor: NetworkStateMonitor
) : ViewModel() {

    val appState: StateFlow<AppState> = combine(
        authStateMonitor.isAuthenticated,
        networkMonitor.isOnline
    ) { isAuthenticated, isOnline ->
        when {
            !isAuthenticated -> AppState.NotAuthenticated
            !isOnline -> AppState.Offline
            else -> AppState.Ready
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppState.NotAuthenticated
    )
}
