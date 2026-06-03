package com.openclassrooms.hexagonal.games.presentation.screen.homefeed

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostsUseCase
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import com.openclassrooms.hexagonal.games.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val posts: StateFlow<List<PostUi>> = getPostsUseCase()
        .map { list -> list.map { it.toPostUi() } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
    val currentUserName: StateFlow<String?> = authStateMonitor.isAuthenticated
        .map { auth.currentUser?.displayName }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            auth.currentUser?.displayName
        )
}
