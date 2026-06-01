package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.usecase.AddCommentUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.DeletePostUseCase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val manageUserUseCase: ManageUserUseCase
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

/*    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()*/

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

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
                //_currentUserId.value = user?.uid
                _uiState.update { it.copy(currentUserId = user?.uid) }
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

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
/*                _isDeleting.value = true
                _deleteError.value = null*/
                _uiState.update { it.copy(isDeleting = true, deleteError = null) }
                deletePostUseCase(postId)
                _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
/*                _deleteSuccess.value = true*/
            } catch (e: Exception) {
                /*_deleteError.value = e.message ?: "Erreur lors de la suppression"*/
                _uiState.update { it.copy(isDeleting = false, deleteError = e.message ?: "Erreur lors de la suppression") }
            /*} finally {
                _isDeleting.value = false*/
            }
        }
    }

    fun resetDeleteState() {
/*        _deleteSuccess.value = false
        _deleteError.value = null*/
        _uiState.update { it.copy(deleteSuccess = false, deleteError = null) }
    }
}
