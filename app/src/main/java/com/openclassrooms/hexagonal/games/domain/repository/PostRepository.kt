package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for managing Posts.
 * This lives in the domain layer.
 */
interface PostRepository {
    /**
     * A Flow of the list of all posts.
     */
    val posts: Flow<List<Post>>

    /**
     * Adds a new post.
     */
    suspend fun addPost(post: Post)

    /**
     * Retrieves a Post by its ID.
     */
    suspend fun getPostById(postId: String): Post?

    /**
     * Deletes a Post by its ID.
     */
    suspend fun deletePost(postId: String)
}
