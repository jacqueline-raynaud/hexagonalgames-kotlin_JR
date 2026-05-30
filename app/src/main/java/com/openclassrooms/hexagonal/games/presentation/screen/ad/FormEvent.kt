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
    data object TitleMissing : FormError(R.string.form_error_title)
    data object DescriptionMissing : FormError(R.string.form_error_description)
    data object ImageMissing : FormError(R.string.form_error_image)
    data object InvalidForm : FormError(R.string.form_error_invalid)
}
