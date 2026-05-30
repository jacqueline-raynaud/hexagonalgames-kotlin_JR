package com.openclassrooms.hexagonal.games.domain.usecase

import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPostByIdUseCaseTest {

    private lateinit var postRepository: PostRepository
    private lateinit var getPostByIdUseCase: GetPostByIdUseCase

    @Before
    fun setUp() {
        postRepository = mockk()
        getPostByIdUseCase = GetPostByIdUseCase(postRepository)
    }

    @Test
    fun `invoke should return post from repository`() = runTest {
        // Given
        val postId = "post123"
        val post = mockk<Post>()
        coEvery { postRepository.getPostById(postId) } returns post

        // When
        val result = getPostByIdUseCase(postId)

        // Then
        assertEquals(post, result)
    }
}
