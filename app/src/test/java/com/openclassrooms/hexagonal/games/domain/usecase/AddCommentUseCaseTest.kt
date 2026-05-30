package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddCommentUseCaseTest {

    private lateinit var commentRepository: CommentRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var addCommentUseCase: AddCommentUseCase

    @Before
    fun setUp() {
        commentRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        addCommentUseCase = AddCommentUseCase(commentRepository, auth)
    }

    @Test
    fun `invoke should save comment when user is logged in`() = runTest {
        // Given
        val userId = "user123"
        val userName = "John Doe"
        val postId = "post456"
        val content = "Test Comment"
        
        val firebaseUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns userId
        every { firebaseUser.displayName } returns userName

        // When
        addCommentUseCase(postId, content)

        // Then
        coVerify {
            commentRepository.addComment(match {
                it.postId == postId &&
                it.content == content &&
                it.author?.id == userId &&
                it.author?.nameUser == userName
            })
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `invoke should throw exception when user is not logged in`() = runTest {
        // Given
        every { auth.currentUser } returns null

        // When
        addCommentUseCase("postId", "content")
    }
}
