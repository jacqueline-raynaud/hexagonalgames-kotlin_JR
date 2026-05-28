package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.openclassrooms.hexagonal.games.data.model.PostDto
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class PostFirestoreApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostApi {

    private val collection = firestore.collection("publications")

    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> =
        callbackFlow {
            val listener = collection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val posts = snapshot?.documents
                        ?.mapNotNull { doc ->
                            // ON RÉCUPÈRE L'ID ICI
                            doc.toObject<PostDto>()?.toDomain()?.copy(id = doc.id)
                        } ?: emptyList()
                    trySend(posts)
                }
            awaitClose { listener.remove() }
        }

    override suspend fun addPost(post: Post) {
        val data = mapOf(
            "title"       to post.title,
            "description" to post.description,
            "photoUrl"    to post.photoUrl,
            "timestamp"   to post.timestamp,
            "author"      to mapOf(
                "id"        to post.author?.id,
                "nameUser" to post.author?.nameUser
            )
        )
        collection.add(data).await()
    }

    override suspend fun getPostById(postId: String): Post? {
        if (postId.isEmpty()) return null
        return try {
            val doc = collection.document(postId).get().await()
            doc.toObject<PostDto>()?.toDomain()?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deletePost(postId: String) {
        collection.document(postId).delete().await()
    }
}
