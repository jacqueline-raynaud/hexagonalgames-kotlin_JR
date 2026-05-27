package com.openclassrooms.hexagonal.games.domain.usecase

import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case to get a post by its ID.
 */
class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Post? {
        return postRepository.getPostById(postId)
    }
}
