package com.openclassrooms.hexagonal.games.domain.usecase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to add a new post.
 * It handles image upload and post creation.
 */
class AddPostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val storageRepository: StorageRepository,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(title: String, description: String, imageUri: Uri?) {
        val firebaseUser = auth.currentUser ?: throw IllegalStateException("User must be logged in")

        // 1. Upload image if present
        val finalPhotoUrl: String? = imageUri?.let { uri ->
            storageRepository.uploadImage(firebaseUser.uid, uri)
        }

        // 2. Create author object
        val author = User(
            id = firebaseUser.uid,
            nameUser = firebaseUser.displayName ?: "Utilisateur"
        )

        // 3. Create post object
        val newPost = Post(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            photoUrl = finalPhotoUrl,
            timestamp = System.currentTimeMillis(),
            author = author
        )

        // 4. Save to repository
        postRepository.addPost(newPost)
    }
}
