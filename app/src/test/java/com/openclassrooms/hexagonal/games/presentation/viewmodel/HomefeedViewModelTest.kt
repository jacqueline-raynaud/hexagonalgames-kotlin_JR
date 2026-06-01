package com.openclassrooms.hexagonal.games.presentation.viewmodel

import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostsUseCase
import com.openclassrooms.hexagonal.games.domain.util.AuthStateMonitor
import com.openclassrooms.hexagonal.games.domain.util.NetworkStateMonitor
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.HomefeedViewModel
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.PostUi
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
    fun `posts should expose mapped PostUi from getPostsUseCase`() = runTest {
        // Given
        val author = User(id = "u1", nameUser = "Alice Dupont")
        val domainPosts = listOf(
            Post(
                id = "1",
                title = "Post 1",
                description = "Desc 1",
                photoUrl = null,
                timestamp = 0,
                author = author
            ),
            Post(
                id = "2",
                title = "Post 2",
                description = null,
                photoUrl = "https://example.com/img.jpg",
                timestamp = 0,
                author = null
            )
        )
        every { getPostsUseCase() } returns flowOf(domainPosts)

        // When
        viewModel = HomefeedViewModel(authStateMonitor, networkMonitor, getPostsUseCase)
        val emitted = viewModel.posts.first { it.isNotEmpty() }

        // Then
        val expected = listOf(
            PostUi(
                id = "1",
                authorId = "u1",
                authorName = "Alice Dupont",
                title = "Post 1",
                description = "Desc 1",
                photoUrl = null
            ),
            PostUi(
                id = "2",
                authorId = "",
                authorName = "",
                title = "Post 2",
                description = null,
                photoUrl = "https://example.com/img.jpg"
            )
        )
        Assert.assertEquals(expected, emitted)
    }
}