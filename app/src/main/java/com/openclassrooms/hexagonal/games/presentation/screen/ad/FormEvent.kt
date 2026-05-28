package com.openclassrooms.hexagonal.games.presentation.screen.ad

import android.net.Uri
import androidx.annotation.StringRes
import com.openclassrooms.hexagonal.games.R

/**
 * A sealed class representing different events that can occur on a form
 * in AddScreen
 */
sealed class FormEvent {

    data class TitleChanged(val title: String) : FormEvent()
    data class DescriptionChanged(val description: String) : FormEvent()
    data class ImageSelected(val uri: Uri) : FormEvent()
    data object SaveClicked : FormEvent()
}

sealed class FormError(@StringRes val messageRes: Int) {
    data object TitleError : FormError(R.string.error_title)

}
