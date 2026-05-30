package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCommentsQueryUseCaseTest {

    private lateinit var commentRepository: CommentRepository
    private lateinit var getCommentsQueryUseCase: GetCommentsQueryUseCase

    @Before
    fun setUp() {
        commentRepository = mockk()
        getCommentsQueryUseCase = GetCommentsQueryUseCase(commentRepository)
    }

    @Test
    fun `invoke should return query from repository`() {
        // Given
        val postId = "post123"
        val query = mockk<Query>()
        every { commentRepository.getCommentsQuery(postId) } returns query

        // When
        val result = getCommentsQueryUseCase(postId)

        // Then
        assertEquals(query, result)
    }
}
