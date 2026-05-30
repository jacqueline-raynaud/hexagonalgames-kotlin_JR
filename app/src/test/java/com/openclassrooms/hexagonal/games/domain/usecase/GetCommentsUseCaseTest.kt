package com.openclassrooms.hexagonal.games.domain.usecase

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCommentsUseCaseTest {

    private lateinit var commentRepository: CommentRepository
    private lateinit var getCommentsUseCase: GetCommentsUseCase

    @Before
    fun setUp() {
        commentRepository = mockk()
        getCommentsUseCase = GetCommentsUseCase(commentRepository)
    }

    @Test
    fun `invoke should return flow of comments from repository`() = runTest {
        // Given
        val postId = "post123"
        val comments = listOf(mockk<Comment>(), mockk<Comment>())
        every { commentRepository.getComments(postId) } returns flowOf(comments)

        // When
        val result = getCommentsUseCase(postId).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(comments, result[0])
    }
}
