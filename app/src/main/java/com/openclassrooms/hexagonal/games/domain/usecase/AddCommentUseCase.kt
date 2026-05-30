package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to add a new comment.
 */
class AddCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(postId: String, content: String) : Unit = withContext(Dispatchers.IO) {
        val firebaseUser = auth.currentUser ?: throw IllegalStateException("User must be logged in")

        val author = User(
            id = firebaseUser.uid,
            nameUser = firebaseUser.displayName ?: "Utilisateur"
        )

        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            content = content,
            timestamp = System.currentTimeMillis(),
            author = author
        )

        commentRepository.addComment(newComment)
    }
}
