package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for managing Comments.
 */
interface CommentRepository {
    /**
     * Gets a Firestore Query for comments of a specific post, ordered by timestamp.
     */
    fun getCommentsQuery(postId: String): Query

    /**
     * Gets a Flow of comments for a specific post.
     */
    fun getComments(postId: String): Flow<List<Comment>>

    /**
     * Adds a new comment.
     */
    suspend fun addComment(comment: Comment)
}
