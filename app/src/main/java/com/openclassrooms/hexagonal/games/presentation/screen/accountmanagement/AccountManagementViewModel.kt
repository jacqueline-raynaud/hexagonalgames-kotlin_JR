package com.openclassrooms.hexagonal.games.presentation.screen.accountmanagement

import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.domain.usecase.ManageUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for managing user account actions like sign out and account deletion.
 * It utilizes the ManageUserUseCase to handle the business logic.
 */
@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val manageUserUseCase: ManageUserUseCase
) : ViewModel() {

    /**
     * Signs out the current user and executes a callback on success.
     */
    fun signOut(onSuccess: () -> Unit = {}) {
        manageUserUseCase.signOut()
        onSuccess()
    }

    /**
     * Deletes the current user's account and executes appropriate callbacks.
     */
    fun deleteAccount(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        manageUserUseCase.deleteAccount { success, exception ->
            if (success) {
                onSuccess()
            } else {
                onFailure(exception ?: Exception("Unknown error during account deletion"))
            }
        }
    }
}
