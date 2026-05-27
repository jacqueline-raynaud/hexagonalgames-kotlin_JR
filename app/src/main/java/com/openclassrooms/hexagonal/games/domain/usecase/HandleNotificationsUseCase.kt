package com.openclassrooms.hexagonal.games.domain.usecase

import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

/**
 * Use case to handle notification subscriptions.
 */
class HandleNotificationsUseCase @Inject constructor() {

    fun enable() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    fun disable() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
    }
}
