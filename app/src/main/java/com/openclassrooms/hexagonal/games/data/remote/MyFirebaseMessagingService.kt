package com.openclassrooms.hexagonal.games.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassrooms.hexagonal.games.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // recuperation du token dans le log pour test d'envoi
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FIREBASE_MESSAGING_SERVICE", "Refreshed token: $token")
    }

    // appel quand application au premier plan
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
        Log.d("FIREBASE_MESSAGING_SERVICE", "Message received: ${remoteMessage.data}")
    }
    private fun showNotification(title: String?, message: String?) {
        val channelId = "notification_channel"
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java)

        //creation du canal pour android >=8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_notifications)
            .setAutoCancel(true)
            .build()

        notificationManager?.notify(0, notification)
    }
}


