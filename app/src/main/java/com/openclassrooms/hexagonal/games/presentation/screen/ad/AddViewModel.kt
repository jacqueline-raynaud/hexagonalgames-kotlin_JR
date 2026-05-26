package com.openclassrooms.hexagonal.games.presentation.screen.ad

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import com.openclassrooms.hexagonal.games.presentation.BaseViewModel
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    authStateMonitor: AuthStateMonitor,
    networkMonitor: NetworkStateMonitor,
    private val postRepository: PostRepository,
    private val auth: FirebaseAuth,
    private val storageRepository: StorageRepository,
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
            }

            is FormEvent.ImageSelected -> {
                updateState { it.copy(imageUri = formEvent.uri) }
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
     * sauvegarde du Post (Storage puis Firestore)
     */
    fun addPost() {
        val firebaseUser = auth.currentUser ?: return
        val currentState = _uiState.value

        // verifie si le formulaire est valide avant d'envoyer
        if (currentState.title.isBlank()) {
            validateForm()
            return
        }

        viewModelScope.launch {
            try {
                updateState { it.copy(isSaving = true) }

                // upload vers storage
                val finalPhotoUrl: String? = currentState.imageUri?.let { uri ->
                    storageRepository.uploadImage(firebaseUser.uid, uri)
                }

                // Création de l'objet métier final au MOMENT de la sauvegarde
                val author = User(
                    id = firebaseUser.uid,
                    nameUser = firebaseUser.displayName ?: "Utilisateur"
                )

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    title = currentState.title,
                    description = currentState.description,
                    photoUrl = finalPhotoUrl,
                    timestamp = System.currentTimeMillis(),
                    author = author
                )

                postRepository.addPost(newPost)

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



