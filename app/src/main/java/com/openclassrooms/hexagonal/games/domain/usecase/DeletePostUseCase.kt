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
    suspend operator fun invoke(postId: String): Unit = withContext(Dispatchers.IO) {
        try {
            // 1. Retrieve the post to get the image URL
            val post = postRepository.getPostById(postId)

            // 2. Delete associated comments
            commentRepository.deleteCommentsByPostId(postId)

            // 3. Delete Post
            postRepository.deletePost(postId)

            // 4. Delete the image from Storage if it exists
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
