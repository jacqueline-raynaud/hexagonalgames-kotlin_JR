package com.openclassrooms.hexagonal.games.presentation.screen.ad

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.domain.usecase.AddPostUseCase
import com.openclassrooms.hexagonal.games.presentation.BaseViewModel
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
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
                //validateForm()
            }

            is FormEvent.ImageSelected -> {
                updateState { it.copy(imageUri = formEvent.uri) }
                //validateForm()
            }

            is FormEvent.SaveClicked -> {
                addPost()
            }

            is FormEvent.AuthStateChanged -> {
                val isAppReady = formEvent.appState is AppState.Ready
                updateState { currentState ->
                    currentState.copy(isSaveEnabled = currentState.error == null && isAppReady)
                }
            }

        }

    }

    /**
     * Logique de validation du formulaire
     */
    private fun validateForm() {
        updateState { currentState ->
            val hasTitle = currentState.title.isNotBlank()
            val hasDescription = currentState.description.isNotBlank()
            val hasImage = currentState.imageUri != null

            // LeTitre ET Description OU Titre ET Image
            val isFormValid = (hasTitle && hasDescription) || (hasTitle && hasImage)

            // verifie si authentifié et connecté
            val isAppReady = appState.value is AppState.Ready
            Log.d ("AddViewModel", "isAppReady : $isAppReady")

            // Ls deux verif sont ok pour sauver
            val canSave = isFormValid && isAppReady

            // Gestion des messages d'erreur visuels
            val error = if (!hasTitle) FormError.TitleError else null

            currentState.copy(
                error = error,
                isSaveEnabled = canSave
            )
        }
    }

    /**
     * sauvegarde du Post via le UseCase
     */
    fun addPost() {
        val currentState = _uiState.value

        // verifie si le formulaire est valide avant d'envoyer
        if (currentState.title.isBlank()) {
            validateForm()
            return
        }

        viewModelScope.launch {
            try {
                updateState { it.copy(isSaving = true) }

                // Appel au UseCase qui gère tout (upload + création post)
                addPostUseCase(
                    title = currentState.title,
                    description = currentState.description,
                    imageUri = currentState.imageUri
                )

                // pour gérer la durée de l'action quand il y a une image
                updateState { it.copy(isSaved = true) }

            } catch (e: Exception) {
                Log.e("AddViewModel", "Erreur addPost : ${e.message}", e)
            } finally {
                updateState { it.copy(isSaving = false) }
            }
        }
    }

    /**
     * Petite fonction utilitaire pour simplifier la mise à jour du StateFlow
     */
    private inline fun updateState(transform: (AddUiState) -> AddUiState) {
        _uiState.value = transform(_uiState.value)
    }
}
