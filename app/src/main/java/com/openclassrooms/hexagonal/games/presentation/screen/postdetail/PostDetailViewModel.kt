package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.usecase.AddCommentUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.DeletePostUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostByIdUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.ManageUserUseCase
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.PostUi
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.toPostUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val manageUserUseCase: ManageUserUseCase
) : ViewModel() {

    private val _post = MutableStateFlow<PostUi?>(null)
    val post: StateFlow<PostUi?> = _post.asStateFlow()

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private val _postId = MutableStateFlow<String?>(null)


    fun fetchPost(postId: String) {
        viewModelScope.launch {
            _post.value = getPostByIdUseCase(postId)?.toPostUi()
            manageUserUseCase.getUser().collect { user ->
                _appState.value = if (user != null) AppState.Ready else AppState.NotAuthenticated
                _uiState.update { it.copy(currentUserId = user?.uid) }
            }
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                addCommentUseCase(postId, content)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Erreur addComment : ${e.message}", e)
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isDeleting = true, deleteError = null) }
                deletePostUseCase(postId)
                _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteError = e.message ?: "Erreur lors de la suppression"
                    )
                }
            }
        }
    }

    fun resetDeleteState() {
        _uiState.update { it.copy(deleteSuccess = false, deleteError = null) }
    }
}
