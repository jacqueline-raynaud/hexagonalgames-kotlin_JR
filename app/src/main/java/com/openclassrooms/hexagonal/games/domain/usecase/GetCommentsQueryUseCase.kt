package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import javax.inject.Inject

/**
 * Use case to get the Firestore query for comments.
 */
class GetCommentsQueryUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(postId: String): Query {
        return commentRepository.getCommentsQuery(postId)
    }
}
