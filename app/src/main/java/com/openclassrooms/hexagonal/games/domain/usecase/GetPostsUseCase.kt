package com.openclassrooms.hexagonal.games.domain.usecase

import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all posts.
 */
class GetPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(): Flow<List<Post>> {
        return postRepository.posts
    }
}
