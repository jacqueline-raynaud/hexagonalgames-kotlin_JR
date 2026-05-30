package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class HandleNotificationsUseCaseTest {

    private lateinit var handleNotificationsUseCase: HandleNotificationsUseCase
    private lateinit var firebaseMessaging: FirebaseMessaging

    @Before
    fun setUp() {
        mockkStatic(FirebaseMessaging::class)
        firebaseMessaging = mockk(relaxed = true)
        every { FirebaseMessaging.getInstance() } returns firebaseMessaging
        handleNotificationsUseCase = HandleNotificationsUseCase()
    }

    @Test
    fun `enable should subscribe to topic all`() {
        // When
        handleNotificationsUseCase.enable()

        // Then
        verify { firebaseMessaging.subscribeToTopic("all") }
    }

    @Test
    fun `disable should unsubscribe from topic all`() {
        // When
        handleNotificationsUseCase.disable()

        // Then
        verify { firebaseMessaging.unsubscribeFromTopic("all") }
    }
}
