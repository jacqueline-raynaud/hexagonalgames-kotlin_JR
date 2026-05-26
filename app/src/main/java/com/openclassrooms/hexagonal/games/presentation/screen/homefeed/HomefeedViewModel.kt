package com.openclassrooms.hexagonal.games.presentation.screen.homefeed

import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.presentation.BaseViewModel
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel responsible for managing data and events related to the Homefeed.
 * This ViewModel retrieves list of posts from the PostRepository and exposes to the
 * ui them as a reactive StateFlow.
 */
@HiltViewModel
class HomefeedViewModel @Inject constructor(
    authStateMonitor: AuthStateMonitor,
    networkMonitor: NetworkStateMonitor,
    postRepository: PostRepository
) : BaseViewModel(authStateMonitor, networkMonitor) {

    val posts: StateFlow<List<Post>> = postRepository.posts
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
}
