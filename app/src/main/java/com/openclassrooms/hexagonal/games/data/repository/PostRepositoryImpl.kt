package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the PostRepository interface using PostApi.
 */
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {

    override val posts: Flow<List<Post>> = postApi.getPostsOrderByCreationDateDesc()

    override suspend fun addPost(post: Post) {
        postApi.addPost(post)
    }
}
