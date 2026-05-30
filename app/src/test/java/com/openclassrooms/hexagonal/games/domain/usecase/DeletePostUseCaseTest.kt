package com.openclassrooms.hexagonal.games.domain.usecase

import android.util.Log
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeletePostUseCaseTest {

    private lateinit var postRepository: PostRepository
    private lateinit var commentRepository: CommentRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var deletePostUseCase: DeletePostUseCase

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        // Spécification explicite des types pour lever l'ambiguïté des surcharges de Log
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        
        postRepository = mockk(relaxed = true)
        commentRepository = mockk(relaxed = true)
        storageRepository = mockk(relaxed = true)
        deletePostUseCase = DeletePostUseCase(postRepository, commentRepository, storageRepository)
    }

    @Test
    fun `invoke should delete comments, post and image`() = runTest {
        // Given
        val postId = "post123"
        val imageUrl = "http://image.url"
        val post = mockk<Post>()
        every { post.photoUrl } returns imageUrl
        coEvery { postRepository.getPostById(postId) } returns post

        // When
        deletePostUseCase(postId)

        // Then
        coVerify { commentRepository.deleteCommentsByPostId(postId) }
        coVerify { postRepository.deletePost(postId) }
        coVerify { storageRepository.deleteImage(imageUrl) }
    }

    @Test
    fun `invoke should not delete image if post has no photoUrl`() = runTest {
        // Given
        val postId = "post123"
        val post = mockk<Post>()
        every { post.photoUrl } returns null
        coEvery { postRepository.getPostById(postId) } returns post

        // When
        deletePostUseCase(postId)

        // Then
        coVerify { commentRepository.deleteCommentsByPostId(postId) }
        coVerify { postRepository.deletePost(postId) }
        coVerify(exactly = 0) { storageRepository.deleteImage(any()) }
    }
}
