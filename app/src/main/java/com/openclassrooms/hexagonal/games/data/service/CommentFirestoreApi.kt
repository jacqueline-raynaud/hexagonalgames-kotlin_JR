package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentFirestoreApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    private val collection = firestore.collection("comments")


    override suspend fun addComment(comment: Comment): Unit = withContext(Dispatchers.IO) {
        val data = mapOf(
            "postId" to comment.postId,
            "content" to comment.content,
            "timestamp" to comment.timestamp,
            "author" to mapOf(
                "id" to comment.author?.id,
                "nameUser" to comment.author?.nameUser
            )
        )
        collection.add(data).await()
    }

    override suspend fun deleteCommentsByPostId(postId: String): Unit =
        withContext(Dispatchers.IO) {
            val snapshot = collection
                .whereEqualTo("postId", postId)
                .get()
                .await()

            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
}
