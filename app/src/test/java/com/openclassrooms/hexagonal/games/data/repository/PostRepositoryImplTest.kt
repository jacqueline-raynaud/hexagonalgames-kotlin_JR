package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Post
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PostRepositoryImplTest {

    private lateinit var postApi: PostApi
    private lateinit var postRepository: PostRepositoryImpl

    @Before
    fun setUp() {
        postApi = mockk()
        // getPostsOrderByCreationDateDesc is called during PostRepositoryImpl initialization
        every { postApi.getPostsOrderByCreationDateDesc() } returns flowOf(emptyList())
        postRepository = PostRepositoryImpl(postApi)
    }

    @Test
    fun `posts flow should return data from postApi`() = runTest {
        // Given
        val posts = listOf(mockk<Post>(), mockk<Post>())
        every { postApi.getPostsOrderByCreationDateDesc() } returns flowOf(posts)

        // When
        // Re-instantiate because the 'posts' property is initialized at construction
        val repository = PostRepositoryImpl(postApi)
        val result = repository.posts.toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(posts, result[0])
    }

    @Test
    fun `addPost should call postApi addPost`() = runTest {
        // Given
        val post = mockk<Post>()
        coEvery { postApi.addPost(post) } returns Unit

        // When
        postRepository.addPost(post)

        // Then
        coVerify { postApi.addPost(post) }
    }

    @Test
    fun `getPostById should call postApi getPostById`() = runTest {
        // Given
        val postId = "123"
        val post = mockk<Post>()
        coEvery { postApi.getPostById(postId) } returns post

        // When
        val result = postRepository.getPostById(postId)

        // Then
        assertEquals(post, result)
        coVerify { postApi.getPostById(postId) }
    }

    @Test
    fun `deletePost should call postApi deletePost`() = runTest {
        // Given
        val postId = "123"
        coEvery { postApi.deletePost(postId) } returns Unit

        // When
        postRepository.deletePost(postId)

        // Then
        coVerify { postApi.deletePost(postId) }
    }
}
