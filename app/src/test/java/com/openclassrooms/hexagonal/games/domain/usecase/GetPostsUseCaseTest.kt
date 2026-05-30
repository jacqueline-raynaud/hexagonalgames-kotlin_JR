package com.openclassrooms.hexagonal.games.domain.usecase

import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPostsUseCaseTest {

    private lateinit var postRepository: PostRepository
    private lateinit var getPostsUseCase: GetPostsUseCase

    @Before
    fun setUp() {
        postRepository = mockk()
        getPostsUseCase = GetPostsUseCase(postRepository)
    }

    @Test
    fun `invoke should return flow of posts from repository`() = runTest {
        // Given
        val posts = listOf(
            mockk<Post>(),
            mockk<Post>()
        )
        every { postRepository.posts } returns flowOf(posts)

        // When
        val result = getPostsUseCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(posts, result[0])
    }
}
