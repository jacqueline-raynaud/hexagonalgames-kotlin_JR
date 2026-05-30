package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ManageUserUseCaseTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var manageUserUseCase: ManageUserUseCase

    @Before
    fun setUp() {
        auth = mockk(relaxed = true)
        manageUserUseCase = ManageUserUseCase(auth)
    }

    @Test
    fun `getUser should return current user via flow`() = runTest {
        // Given
        val firebaseUser = mockk<FirebaseUser>()
        val listenerSlot = slot<FirebaseAuth.AuthStateListener>()
        every { auth.addAuthStateListener(capture(listenerSlot)) } answers {
            listenerSlot.captured.onAuthStateChanged(auth)
        }
        every { auth.currentUser } returns firebaseUser

        // When
        val result = manageUserUseCase.getUser().first()

        // Then
        assertEquals(firebaseUser, result)
        verify { auth.addAuthStateListener(any()) }
    }

    @Test
    fun `signOut should call auth signOut`() {
        // When
        manageUserUseCase.signOut()

        // Then
        verify { auth.signOut() }
    }

    @Test
    fun `deleteAccount should call user delete and invoke callback`() {
        // Given
        val user = mockk<FirebaseUser>()
        val task = mockk<Task<Void>>()
        val onCompleteListenerSlot = slot<OnCompleteListener<Void>>()
        
        every { auth.currentUser } returns user
        every { user.delete() } returns task
        every { task.addOnCompleteListener(capture(onCompleteListenerSlot)) } returns task
        every { task.isSuccessful } returns true
        every { task.exception } returns null

        var successResult = false
        
        // When
        manageUserUseCase.deleteAccount { success, _ ->
            successResult = success
        }
        
        // Simulate completion
        onCompleteListenerSlot.captured.onComplete(task)

        // Then
        assertTrue(successResult)
        verify { user.delete() }
    }
}
