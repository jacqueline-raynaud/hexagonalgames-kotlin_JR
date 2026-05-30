package com.openclassrooms.hexagonal.games.presentation.viewmodel

import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostsUseCase
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.HomefeedViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomefeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authStateMonitor: AuthStateMonitor
    private lateinit var networkMonitor: NetworkStateMonitor
    private lateinit var getPostsUseCase: GetPostsUseCase
    private lateinit var viewModel: HomefeedViewModel

    @Before
    fun setUp() {
        authStateMonitor = mockk(relaxed = true)
        networkMonitor = mockk(relaxed = true)
        getPostsUseCase = mockk()

        every { authStateMonitor.isAuthenticated } returns MutableStateFlow(true)
        every { networkMonitor.isOnline } returns MutableStateFlow(true)
    }

    @Test
    fun `posts should expose data from getPostsUseCase`() = runTest {
        // Given
        val mockPosts = listOf(mockk<Post>(), mockk<Post>())
        every { getPostsUseCase() } returns flowOf(mockPosts)


        // When
        viewModel = HomefeedViewModel(authStateMonitor, networkMonitor, getPostsUseCase)

        val emitted = viewModel.posts.first { it.isNotEmpty() } // force stateIn

        // Then
        Assert.assertEquals(mockPosts, viewModel.posts.value)
    }
}