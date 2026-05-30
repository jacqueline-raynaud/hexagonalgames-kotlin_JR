package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.openclassrooms.hexagonal.games.data.model.CommentDto
import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentFirestoreApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentApi {

    private val collection = firestore.collection("comments")

    override fun getCommentsQuery(postId: String): Query {
        return collection
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
    }

    override fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = getCommentsQuery(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents
                    ?.mapNotNull { it.toObject<CommentDto>() }
                    ?.map { it.toDomain() }
                    ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(comment: Comment) : Unit = withContext(Dispatchers.IO) {
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

    override suspend fun deleteCommentsByPostId(postId: String) : Unit = withContext(Dispatchers.IO) {
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
