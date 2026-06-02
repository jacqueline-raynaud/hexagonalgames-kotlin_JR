package com.openclassrooms.hexagonal.games.data.service

import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * This interface defines the contract for interacting with Post data from a data source.
 * It outlines the methods for retrieving and adding Posts, abstracting the underlying
 * implementation details of fetching and persisting data.
 */
interface PostApi {
  fun getPostsOrderByCreationDateDesc(): Flow<List<Post>>
  suspend fun addPost(post: Post)
  suspend fun getPostById(postId: String): Post?
  suspend fun deletePost(postId: String)
}
