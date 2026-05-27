package com.openclassrooms.hexagonal.games.domain.usecase

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get the comments of a post in real-time.
 */
class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(postId: String): Flow<List<Comment>> {
        return commentRepository.getComments(postId)
    }
}
