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


        // [END auth_fui_result]

        /*private fun signOut() {
    // [START auth_fui_signout]
    AuthUI.getInstance()
        .signOut(this)
        .addOnCompleteListener {
            // ...
        }
    // [END auth_fui_signout]
}

private fun delete() {
    // [START auth_fui_delete]
    AuthUI.getInstance()
        .delete(this)
        .addOnCompleteListener {
            // ...
        }
    // [END auth_fui_delete]
}

private fun themeAndLogo() {
    val providers = emptyList<AuthUI.IdpConfig>()

    // [START auth_fui_theme_logo]
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setLogo(R.drawable.my_great_logo) // Set logo drawable
        .setTheme(R.style.MySuperAppTheme) // Set theme
        .build()
    signInLauncher.launch(signInIntent)
    // [END auth_fui_theme_logo]
}

private fun privacyAndTerms() {
    val providers = emptyList<AuthUI.IdpConfig>()
    // [START auth_fui_pp_tos]
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setTosAndPrivacyPolicyUrls(
            "https://example.com/terms.html",
            "https://example.com/privacy.html",
        )
        .build()
    signInLauncher.launch(signInIntent)
    // [END auth_fui_pp_tos]
}*/
    }
}