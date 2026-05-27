package com.openclassrooms.hexagonal.games.presentation.screen.settings

import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.domain.usecase.HandleNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val handleNotificationsUseCase: HandleNotificationsUseCase
) : ViewModel() {

    fun enableNotifications() {
        handleNotificationsUseCase.enable()
    }

    fun disableNotifications() {
        handleNotificationsUseCase.disable()
    }
}
