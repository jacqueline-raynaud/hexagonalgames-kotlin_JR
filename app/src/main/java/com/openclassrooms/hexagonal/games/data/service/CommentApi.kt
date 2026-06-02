package com.openclassrooms.hexagonal.games.data.service

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with Comment data.
 */
interface CommentApi {

    /**
     * Adds a new Comment.
     */
    suspend fun addComment(comment: Comment)

    /**
     * Deletes all comments for a specific post.
     */
    suspend fun deleteCommentsByPostId(postId: String)
}
