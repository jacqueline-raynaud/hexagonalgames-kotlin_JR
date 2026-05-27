package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.usecase.AddCommentUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.GetCommentsUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostByIdUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.ManageUserUseCase
import com.openclassrooms.hexagonal.games.domain.util.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val manageUserUseCase: ManageUserUseCase
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _postId = MutableStateFlow<String?>(null)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val comments: StateFlow<List<Comment>> = _postId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else getCommentsUseCase(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun fetchPost(postId: String) {
        _postId.value = postId
        viewModelScope.launch {
            _post.value = getPostByIdUseCase(postId)
            manageUserUseCase.getUser().collect { user ->
                _appState.value = if (user != null) AppState.Ready else AppState.NotAuthenticated
            }
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                addCommentUseCase(postId, content)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
