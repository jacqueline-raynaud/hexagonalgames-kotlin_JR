package com.openclassrooms.hexagonal.games.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.R

/**
 * Activité gérant l'authentification via Firebase UI.
 * Utilise un thème avec ActionBar pour éviter les chevauchements de titre sur les champs de saisie.
 */
class FirebaseUiActivity : AppCompatActivity() {

    /**
     * Lanceur d'activité pour le résultat de l'authentification Firebase UI.
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lancer l'écran de connexion
        createSignInIntent()
    }

    private fun createSignInIntent() {
        // choix du ou des providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
        )

        // Créer l'intent avec le thème possédant une ActionBar pour éviter les chevauchements de titre
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_FirebaseUI)
            .build()

        signInLauncher.launch(signInIntent)
    }

    // reception du résultat de l'intent de connexion
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Authentification réussie
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("FirebaseUI", "Utilisateur authentifié : ${user?.email}")

            // Redirection vers MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            if (response == null) {
                // L'utilisateur a annulé le processus (touche retour)
                finish()
            } else {
                // Erreur d'authentification
                val errorCode = response.error?.errorCode
                Log.e("FirebaseUI", "Sign-in error: $errorCode")
            }
        }
    }
}
