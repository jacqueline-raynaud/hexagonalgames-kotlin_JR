package com.openclassrooms.hexagonal.games.presentation.viewmodel

import com.openclassrooms.hexagonal.games.domain.usecase.ManageUserUseCase
import com.openclassrooms.hexagonal.games.presentation.screen.accountmanagement.AccountManagementViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AccountManagementViewModelTest {

    private lateinit var manageUserUseCase: ManageUserUseCase
    private lateinit var viewModel: AccountManagementViewModel

    @Before
    fun setUp() {
        manageUserUseCase = mockk(relaxed = true)
        viewModel = AccountManagementViewModel(manageUserUseCase)
    }

    @Test
    fun `signOut should call usecase signOut and invoke callback`() {
        // Given
        var callbackInvoked = false

        // When
        viewModel.signOut(onSuccess = { callbackInvoked = true })

        // Then
        verify { manageUserUseCase.signOut() }
        Assert.assertTrue(callbackInvoked)
    }

    @Test
    fun `deleteAccount should call usecase deleteAccount and invoke success callback`() {
        // Given
        val onCompleteSlot = slot<(Boolean, Exception?) -> Unit>()
        every { manageUserUseCase.deleteAccount(capture(onCompleteSlot)) } answers {
            onCompleteSlot.captured(true, null)
        }
        var successInvoked = false

        // When
        viewModel.deleteAccount(onSuccess = { successInvoked = true })

        // Then
        verify { manageUserUseCase.deleteAccount(any()) }
        Assert.assertTrue(successInvoked)
    }

    @Test
    fun `deleteAccount should call usecase deleteAccount and invoke failure callback`() {
        // Given
        val exception = Exception("Delete failed")
        val onCompleteSlot = slot<(Boolean, Exception?) -> Unit>()
        every { manageUserUseCase.deleteAccount(capture(onCompleteSlot)) } answers {
            onCompleteSlot.captured(false, exception)
        }
        var failureInvoked = false

        // When
        viewModel.deleteAccount(onFailure = { failureInvoked = true })

        // Then
        verify { manageUserUseCase.deleteAccount(any()) }
        Assert.assertTrue(failureInvoked)
    }
}