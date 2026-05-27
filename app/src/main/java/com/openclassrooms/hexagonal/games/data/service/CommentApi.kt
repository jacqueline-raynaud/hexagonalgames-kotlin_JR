package com.openclassrooms.hexagonal.games.data.service

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with Comment data.
 */
interface CommentApi {
    /**
     * Gets a Firestore Query for comments of a specific post.
     */
    fun getCommentsQuery(postId: String): Query

    /**
     * Gets a Flow of comments for a specific post.
     */
    fun getComments(postId: String): Flow<List<Comment>>

    /**
     * Adds a new Comment.
     */
    suspend fun addComment(comment: Comment)
}
