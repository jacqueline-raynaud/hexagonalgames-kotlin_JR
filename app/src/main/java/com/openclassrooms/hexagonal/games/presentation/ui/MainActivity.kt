package com.openclassrooms.hexagonal.games.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.presentation.screen.Screen
import com.openclassrooms.hexagonal.games.presentation.screen.accountmanagement.AccountManagementScreen
import com.openclassrooms.hexagonal.games.presentation.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.presentation.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.presentation.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            _root_ide_package_.com.openclassrooms.hexagonal.games.presentation.ui.theme.HexagonalGamesTheme {
                HexagonalGamesNavHost(navHostController = navController)
            }
        }
    }
}

@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
    val context = LocalContext.current // pour ma boite dialogue erreur
    NavHost(
        navController = navHostController,
        startDestination = Screen.Homefeed.route
    ) {
        composable(route = Screen.Homefeed.route) {
            HomefeedScreen(
                onPostClick = {
                    //TODO
                },
                onSettingsClick = {
                    navHostController.navigate(Screen.Settings.route)
                },
              onAccountManagementClick = {
                  navHostController.navigate(Screen.AccountManagement.route)
              },
                onFABClick = {
                    navHostController.navigate(Screen.AddPost.route)
                },
                onNavigateToLogin = {
                    val intent = Intent(context, FirebaseUiActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
        composable(route = Screen.AddPost.route) {
            AddScreen(
                onBackClick = { navHostController.navigateUp() },
                onSaveClick = { navHostController.navigateUp() }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navHostController.navigateUp() }
            )
        }
        composable(route = Screen.AccountManagement.route) {
            AccountManagementScreen(
                onBackClick = { navHostController.navigateUp() }
            )
        }

    }
}
