package com.openclassrooms.hexagonal.games.presentation.screen.ad

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class AddUiState(
    val title: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val isSaving: Boolean = false,
    val error: FormError? = null,
    val isSaved: Boolean = false,
    val showAuthError: Boolean = false
)