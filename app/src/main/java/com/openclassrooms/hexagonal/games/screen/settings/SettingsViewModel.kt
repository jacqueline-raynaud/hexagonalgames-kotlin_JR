package com.openclassrooms.hexagonal.games.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
class SettingsViewModel : ViewModel() {

  fun enableNotifications() {
    FirebaseMessaging.getInstance().subscribeToTopic("all")
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Log.d("FCM", "Abonné aux notifications")
        }

      }
  }

  fun disableNotifications() {
    FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Log.d("FCM", "Désabonné des notifications")
        }
      }
  }
  
}
