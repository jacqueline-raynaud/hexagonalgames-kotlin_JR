package com.openclassrooms.hexagonal.games.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class StorageRepositoryImplTest {

    private lateinit var storage: FirebaseStorage
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var storageRepository: StorageRepositoryImpl

    @Before
    fun setUp() {
        storage = mockk()
        context = mockk()
        contentResolver = mockk()
        every { context.contentResolver } returns contentResolver
        storageRepository = StorageRepositoryImpl(storage, context)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `uploadImage should upload bytes and return download url`() = runTest {
        // Given
        val userId = "user123"
        val uri = mockk<Uri>()
        val bytes = "test content".toByteArray()
        val inputStream = ByteArrayInputStream(bytes)
        val downloadUri = mockk<Uri>()
        val downloadUrl = "http://firebase.storage/image.jpg"
        
        val rootRef = mockk<StorageReference>()
        val fileRef = mockk<StorageReference>()
        val uploadTask = mockk<UploadTask>()
        val uriTask = mockk<Task<Uri>>()

        every { contentResolver.openInputStream(uri) } returns inputStream
        every { storage.reference } returns rootRef
        every { rootRef.child(match { it.startsWith("posts/$userId/") }) } returns fileRef
        
        every { fileRef.putBytes(any()) } returns uploadTask
        coEvery { uploadTask.await() } returns mockk()
        
        every { fileRef.downloadUrl } returns uriTask
        coEvery { uriTask.await() } returns downloadUri
        every { downloadUri.toString() } returns downloadUrl

        // When
        val result = storageRepository.uploadImage(userId, uri)

        // Then
        assertEquals(downloadUrl, result)
        coVerify { fileRef.putBytes(any()) }
        coVerify { fileRef.downloadUrl }
    }

    @Test
    fun `deleteImage should delete file from storage`() = runTest {
        // Given
        val downloadUrl = "https://firebasestorage.googleapis.com/v0/b/bucket/o/posts%2Fuser123%2Fimage.jpg?alt=media"
        val path = "posts/user123/image.jpg"
        val rootRef = mockk<StorageReference>()
        val fileRef = mockk<StorageReference>()
        val deleteTask = mockk<Task<Void>>()

        every { storage.reference } returns rootRef
        every { rootRef.child(path) } returns fileRef
        every { fileRef.delete() } returns deleteTask
        coEvery { deleteTask.await() } returns mockk()

        // When
        storageRepository.deleteImage(downloadUrl)

        // Then
        coVerify { rootRef.child(path) }
        coVerify { fileRef.delete() }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `deleteImage should throw exception for invalid URL`() = runTest {
        // Given
        val invalidUrl = "https://example.com/not-a-firebase-url"

        // When
        storageRepository.deleteImage(invalidUrl)
    }
}
