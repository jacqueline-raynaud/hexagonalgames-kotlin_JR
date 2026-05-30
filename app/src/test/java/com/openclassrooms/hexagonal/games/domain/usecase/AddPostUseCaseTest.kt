package com.openclassrooms.hexagonal.games.domain.usecase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddPostUseCaseTest {

    private lateinit var postRepository: PostRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var addPostUseCase: AddPostUseCase

    @Before
    fun setUp() {
        postRepository = mockk(relaxed = true)
        storageRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        addPostUseCase = AddPostUseCase(postRepository, storageRepository, auth)
    }

    @Test
    fun `invoke should upload image and save post when user is logged in`() = runTest {
        // Given
        val userId = "user123"
        val userName = "John Doe"
        val title = "Test Post"
        val description = "Test Description"
        val imageUri = mockk<Uri>()
        val imageUrl = "http://image.url"
        
        val firebaseUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns userId
        every { firebaseUser.displayName } returns userName
        
        coEvery { storageRepository.uploadImage(userId, imageUri) } returns imageUrl

        // When
        addPostUseCase(title, description, imageUri)

        // Then
        coVerify { storageRepository.uploadImage(userId, imageUri) }
        coVerify {
            postRepository.addPost(match {
                it.title == title &&
                it.description == description &&
                it.photoUrl == imageUrl &&
                it.author?.id == userId &&
                it.author?.nameUser == userName
            })
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `invoke should throw exception when user is not logged in`() = runTest {
        // Given
        every { auth.currentUser } returns null

        // When
        addPostUseCase("Title", "Description", null)
    }
}
