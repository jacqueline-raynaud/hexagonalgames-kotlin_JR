package com.openclassrooms.hexagonal.games.domain.usecase

import android.util.Log
import com.openclassrooms.hexagonal.games.domain.repository.CommentRepository
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(postId: String) : Unit = withContext(Dispatchers.IO) {
        try {
            // 1. Récupère le post pour obtenir l'URL de l'image
            val post = postRepository.getPostById(postId)

            // 2. Supprime les commentaires associés
            commentRepository.deleteCommentsByPostId(postId)

            // 3. Supprime le post
            postRepository.deletePost(postId)

            // 4. Supprime l'image de Storage si elle existe
            post?.photoUrl?.let { url ->
                try {
                    storageRepository.deleteImage(url)
                } catch (e: Exception) {
                    Log.w("DeletePostUseCase", "Failed to delete image from storage: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("DeletePostUseCase", "Error deleting post: ${e.message}", e)
            throw e
        }
    }
}
