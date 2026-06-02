package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for managing Comments.
 */
interface CommentRepository {
        /**
     * Adds a new comment.
     */
    suspend fun addComment(comment: Comment)

    /**
     * Deletes all comments for a specific post.
     */
    suspend fun deleteCommentsByPostId(postId: String)
}
