package com.openclassrooms.hexagonal.games.presentation.viewmodel

import android.net.Uri
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.domain.usecase.AddPostUseCase
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import com.openclassrooms.hexagonal.games.presentation.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.presentation.screen.ad.FormError
import com.openclassrooms.hexagonal.games.presentation.screen.ad.FormEvent
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authStateMonitor: AuthStateMonitor
    private lateinit var networkMonitor: NetworkStateMonitor
    private lateinit var addPostUseCase: AddPostUseCase
    private lateinit var viewModel: AddViewModel

    private val isAuthenticatedFlow = MutableStateFlow(true)
    private val isOnlineFlow = MutableStateFlow(true)

    @Before
    fun setUp() {
        authStateMonitor = mockk()
        networkMonitor = mockk()
        addPostUseCase = mockk(relaxed = true)

        every { authStateMonitor.isAuthenticated } returns isAuthenticatedFlow
        every { networkMonitor.isOnline } returns isOnlineFlow

        viewModel = AddViewModel(authStateMonitor, networkMonitor, addPostUseCase)
    }

    @Test
    fun `onAction TitleChanged should update state`() {
        // When
        viewModel.onAction(FormEvent.TitleChanged("New Title"))

        // Then
        Assert.assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `addPost should call useCase when state is Ready and form is valid`() = runTest {
        // Given
        viewModel.onAction(FormEvent.TitleChanged("Title"))
        viewModel.onAction(FormEvent.DescriptionChanged("Description"))
        val uri = mockk<Uri>()
        viewModel.onAction(FormEvent.ImageSelected(uri))

        viewModel.appState.first { it is AppState.Ready }

        // When
        viewModel.addPost()

        testScheduler.advanceUntilIdle() // Wait for the coroutine to complete

        // Then
        coVerify { addPostUseCase("Title", "Description", uri) }
        Assert.assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `addPost should show auth error when not authenticated`() {
        // Given
        isAuthenticatedFlow.value = false

        // When
        viewModel.addPost()

        // Then
        Assert.assertTrue(viewModel.uiState.value.showAuthError)
        coVerify(exactly = 0) { addPostUseCase(any(), any(), any()) }
    }

    @Test
    fun `validateForm should set error when title is blank`() {
        // When
        viewModel.onAction(FormEvent.TitleChanged(""))

        // Then
        Assert.assertEquals(FormError.TitleMissing, viewModel.uiState.value.error)
    }
}