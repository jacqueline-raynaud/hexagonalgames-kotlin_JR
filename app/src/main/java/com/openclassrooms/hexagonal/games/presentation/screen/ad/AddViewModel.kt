package com.openclassrooms.hexagonal.games.presentation.screen.ad

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.usecase.AddPostUseCase
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import com.openclassrooms.hexagonal.games.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes the AddPostUseCase to handle the business logic of creating a post.
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    authStateMonitor: AuthStateMonitor,
    networkMonitor: NetworkStateMonitor,
    private val addPostUseCase: AddPostUseCase,
) : BaseViewModel(authStateMonitor, networkMonitor) {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    /**
     * actions utilisateur provenant de l'écran.
     */
    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.TitleChanged -> {
                updateState { it.copy(title = formEvent.title) }
                validateForm()
            }

            is FormEvent.DescriptionChanged -> {
                updateState { it.copy(description = formEvent.description) }
                validateForm()
            }

            is FormEvent.ImageSelected -> {
                updateState { it.copy(imageUri = formEvent.uri) }
                validateForm()
            }

            is FormEvent.SaveClicked -> {
                addPost()
            }
        }
    }

    private fun validateForm() {
        updateState { currentState ->
            val error = when {
                currentState.title.isBlank() -> FormError.TitleMissing
                currentState.description.isBlank() && currentState.imageUri == null -> FormError.InvalidForm
                else -> null
            }
            currentState.copy(error = error)
        }
    }

    private fun getFormError(state: AddUiState): FormError? {
        val hasTitle = state.title.isNotBlank()
        val hasDescription = state.description.isNotBlank()
        val hasImage = state.imageUri != null

        return when {
            !hasTitle -> FormError.TitleMissing
            !hasDescription && !hasImage -> FormError.InvalidForm
            else -> null
        }
    }

    fun addPost() {
        val currentState = _uiState.value

        // first control uathentication
        if (appState.value !is AppState.Ready) {
            updateState { it.copy(showAuthError = true) }
            return
        }

        // valid form
        val formError = getFormError(currentState)
        if (formError != null) {
            updateState { it.copy(error = formError) }
            return
        }

        viewModelScope.launch {
            try {
                updateState { it.copy(isSaving = true) }

                addPostUseCase(
                    title = currentState.title,
                    description = currentState.description,
                    imageUri = currentState.imageUri
                )

                updateState { it.copy(isSaved = true) }

            } catch (e: Exception) {
                Log.e("AddViewModel", "Erreur addPost : ${e.message}", e)
            } finally {
                updateState { it.copy(isSaving = false) }
            }
        }
    }

    fun dismissAuthError() {
        updateState { it.copy(showAuthError = false) }
    }

    private inline fun updateState(transform: (AddUiState) -> AddUiState) {
        _uiState.value = transform(_uiState.value)
    }
}
