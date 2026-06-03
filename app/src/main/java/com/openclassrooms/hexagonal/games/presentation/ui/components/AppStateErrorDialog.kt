package com.openclassrooms.hexagonal.games.presentation.ui.components


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.util.AppState

@Composable
fun AppStateErrorDialog(
    appStatus: AppState,
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.error_title)) },
        text = {
            Text(
                when (appStatus) {
                    AppState.NotAuthenticated -> stringResource(R.string.error_not_authenticated)
                    AppState.Offline -> stringResource(R.string.error_offline)
                    AppState.Ready -> ""
                    AppState.Loading -> ""
                    AppState.Error -> stringResource(R.string.error_generic)
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            // 3. On affiche ce bouton UNIQUEMENT si l'utilisateur n'est pas connecté
            if (appStatus == AppState.NotAuthenticated) {
                TextButton(onClick = {
                    onDismiss() // On ferme d'abord la boîte de dialogue
                    onNavigateToLogin() // Puis on navigue vers la page de connexion
                }) {
                    Text(stringResource(R.string.login_action)) // Ex: "Se connecter"
                }
            }
        }
    )
}