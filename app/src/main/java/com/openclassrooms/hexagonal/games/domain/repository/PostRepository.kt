package com.openclassrooms.hexagonal.games.domain.repository

import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for managing Posts.
 * This lives in the domain layer.
 */
interface PostRepository {

    val posts: Flow<List<Post>>
    suspend fun addPost(post: Post)
    suspend fun getPostById(postId: String): Post?
    suspend fun deletePost(postId: String)
}
