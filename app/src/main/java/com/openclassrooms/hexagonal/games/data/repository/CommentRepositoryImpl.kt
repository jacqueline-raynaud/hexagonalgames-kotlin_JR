package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.CommentApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi
) : CommentRepository {


    override suspend fun addComment(comment: Comment) {
        commentApi.addComment(comment)
    }

    override suspend fun deleteCommentsByPostId(postId: String) {
        commentApi.deleteCommentsByPostId(postId)
    }
}
