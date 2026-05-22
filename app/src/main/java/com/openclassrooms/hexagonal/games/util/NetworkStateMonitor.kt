package com.openclassrooms.hexagonal.games.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors the network connectivity state of the application.
 *
 * This utility class is responsible for tracking changes in network availability
 * and providing information on whether the device is currently connected to the internet.
 */
@Singleton
class NetworkStateMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val isOnline: StateFlow<Boolean> = callbackFlow {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
        }
        manager.registerDefaultNetworkCallback(callback)
        // valeur initiale
        trySend(manager.activeNetwork != null)
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
}