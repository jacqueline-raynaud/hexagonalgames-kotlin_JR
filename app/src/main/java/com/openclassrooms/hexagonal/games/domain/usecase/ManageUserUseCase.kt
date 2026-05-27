package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Use case to manage user account actions like sign out and account deletion.
 */
class ManageUserUseCase @Inject constructor(
    private val auth: FirebaseAuth
) {
    /**
     * Returns a Flow of the current FirebaseUser.
     */
    fun getUser(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Deletes the current user's account.
     *
     * @param onComplete Callback invoked when the deletion process is complete.
     */
    fun deleteAccount(onComplete: (Boolean, Exception?) -> Unit) {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception)
        } ?: onComplete(false, Exception("No user logged in"))
    }
}
