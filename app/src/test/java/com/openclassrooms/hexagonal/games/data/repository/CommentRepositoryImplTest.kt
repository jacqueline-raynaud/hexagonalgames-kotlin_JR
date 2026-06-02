package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.data.service.CommentApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
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

class CommentRepositoryImplTest {

    private lateinit var commentApi: CommentApi
    private lateinit var commentRepository: CommentRepositoryImpl

    @Before
    fun setUp() {
        commentApi = mockk()
        commentRepository = CommentRepositoryImpl(commentApi)
    }

    @Test
    fun `addComment should call commentApi addComment`() = runTest {
        // Given
        val comment = mockk<Comment>()
        coEvery { commentApi.addComment(comment) } returns Unit

        // When
        commentRepository.addComment(comment)

        // Then
        coVerify { commentApi.addComment(comment) }
    }

    @Test
    fun `deleteCommentsByPostId should call commentApi deleteCommentsByPostId`() = runTest {
        // Given
        val postId = "post123"
        coEvery { commentApi.deleteCommentsByPostId(postId) } returns Unit

        // When
        commentRepository.deleteCommentsByPostId(postId)

        // Then
        coVerify { commentApi.deleteCommentsByPostId(postId) }
    }
}
