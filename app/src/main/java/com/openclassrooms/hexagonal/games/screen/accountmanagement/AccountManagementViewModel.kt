package com.openclassrooms.hexagonal.games.screen.accountmanagement

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AccountManagementViewModel : ViewModel() {

    fun signOut(onSuccess: () -> Unit = {}) {
        FirebaseAuth.getInstance().signOut()
        onSuccess()  // Appeler le callback après la déconnexion
    }

    fun deleteAccount(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()  // Suppression réussie
                } else {
                    onFailure(task.exception ?: Exception("Erreur inconnue"))
                }
            }
    }
}