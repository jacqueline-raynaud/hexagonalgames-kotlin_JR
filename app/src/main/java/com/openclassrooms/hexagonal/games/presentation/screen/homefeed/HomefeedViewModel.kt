package com.openclassrooms.hexagonal.games.presentation.screen.homefeed

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostsUseCase
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import com.openclassrooms.hexagonal.games.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel responsible for managing data and events related to the Homefeed.
 * This ViewModel retrieves list of posts from the GetPostsUseCase and exposes to the
 * ui them as a reactive StateFlow.
 */
@HiltViewModel
class HomefeedViewModel @Inject constructor(
    private val authStateMonitor: AuthStateMonitor,
    networkMonitor: NetworkStateMonitor,
    getPostsUseCase: GetPostsUseCase,
    private val auth: FirebaseAuth
) : BaseViewModel(authStateMonitor, networkMonitor) {

    private val _showMenu = MutableStateFlow(false)
    private val _showError = MutableStateFlow(false)

    val uiState: StateFlow<HomefeedUiState> = combine(
        getPostsUseCase().map { list -> list.map { it.toPostUi() } },
        authStateMonitor.isAuthenticated.map { auth.currentUser?.displayName },
        _showMenu,
        _showError,
        appState
    ) { posts, currentUserName, showMenu, showError, appState ->
        HomefeedUiState(
            posts = posts,
            currentUserName = currentUserName,
            showMenu = showMenu,
            showError = showError,
            appState = appState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomefeedUiState()
    )

    fun toggleMenu() {
        _showMenu.update { !it }
    }

    fun setMenuVisible(visible: Boolean) {
        _showMenu.value = visible
    }

    fun setShowError(show: Boolean) {
        _showError.value = show
    }
}
