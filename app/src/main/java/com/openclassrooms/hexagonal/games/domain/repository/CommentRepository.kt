package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Comment

/**
 * Interface defining the operations for managing Comments.
 */
interface CommentRepository {
    suspend fun addComment(comment: Comment)
    suspend fun deleteCommentsByPostId(postId: String)
}
