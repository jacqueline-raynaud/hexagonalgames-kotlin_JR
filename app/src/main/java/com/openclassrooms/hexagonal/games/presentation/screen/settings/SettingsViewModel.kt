package com.openclassrooms.hexagonal.games.presentation.screen.settings

import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.domain.usecase.HandleNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val handleNotificationsUseCase: HandleNotificationsUseCase
) : ViewModel() {

    private val _areNotificationsEnabled = MutableStateFlow(true)
    val areNotificationsEnabled: StateFlow<Boolean> = _areNotificationsEnabled.asStateFlow()

    fun enableNotifications() {
        handleNotificationsUseCase.enable()
        _areNotificationsEnabled.value = true
    }

    fun disableNotifications() {
        handleNotificationsUseCase.disable()
        _areNotificationsEnabled.value = false
    }
}
