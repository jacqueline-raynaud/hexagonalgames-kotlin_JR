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

    // Référence à la collection Firestore
    private val collection = firestore.collection("publications")

    // callbackFlow convertit le listener Firestore en Flow Kotlin
    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> =
        callbackFlow {
            val listener = collection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)       // ← ferme le Flow en cas d'erreur
                        return@addSnapshotListener
                    }
                    val posts = snapshot?.documents
                        ?.mapNotNull { it.toObject<PostDto>() }
                        ?.map { it.toDomain() }
                        ?: emptyList()
                    trySend(posts)         // ← émet dans le Flow
                }
            awaitClose { listener.remove() }  // ← nettoyage quand le Flow est annulé
        }

    override suspend fun addPost(post: Post) {
        // Convertit le domain model en Map pour Firestore
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
        // .await() = version suspend de la Task Firebase
        collection.add(data).await()
    }
}