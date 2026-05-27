package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.data.service.CommentApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi
) : CommentRepository {

    override fun getCommentsQuery(postId: String): Query {
        return commentApi.getCommentsQuery(postId)
    }

    override fun getComments(postId: String): Flow<List<Comment>> {
        return commentApi.getComments(postId)
    }

    override suspend fun addComment(comment: Comment) {
        commentApi.addComment(comment)
    }
}
