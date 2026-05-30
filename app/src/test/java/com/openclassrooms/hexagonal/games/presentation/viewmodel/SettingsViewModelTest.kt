package com.openclassrooms.hexagonal.games.presentation.viewmodel

import com.openclassrooms.hexagonal.games.domain.usecase.HandleNotificationsUseCase
import com.openclassrooms.hexagonal.games.presentation.screen.settings.SettingsViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var handleNotificationsUseCase: HandleNotificationsUseCase
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        handleNotificationsUseCase = mockk(relaxed = true)
        viewModel = SettingsViewModel(handleNotificationsUseCase)
    }

    @Test
    fun `enableNotifications should call usecase enable`() {
        // When
        viewModel.enableNotifications()

        // Then
        verify { handleNotificationsUseCase.enable() }
    }

    @Test
    fun `disableNotifications should call usecase disable`() {
        // When
        viewModel.disableNotifications()

        // Then
        verify { handleNotificationsUseCase.disable() }
    }
}