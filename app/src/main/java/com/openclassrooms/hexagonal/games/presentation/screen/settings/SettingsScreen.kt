package com.openclassrooms.hexagonal.games.presentation.screen.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.presentation.ui.theme.HexagonalGamesTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val areNotificationsEnabled by viewModel.areNotificationsEnabled.collectAsStateWithLifecycle()

    SettingsScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        areNotificationsEnabled = areNotificationsEnabled,
        onNotificationEnabledClicked = { viewModel.enableNotifications() },
        onNotificationDisabledClicked = { viewModel.disableNotifications() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    areNotificationsEnabled: Boolean,
    onNotificationEnabledClicked: () -> Unit,
    onNotificationDisabledClicked: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.action_settings))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Settings(
            modifier = Modifier.padding(contentPadding),
            areNotificationsEnabled = areNotificationsEnabled,
            onNotificationDisabledClicked = onNotificationDisabledClicked,
            onNotificationEnabledClicked = onNotificationEnabledClicked
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Settings(
    modifier: Modifier = Modifier,
    areNotificationsEnabled: Boolean,
    onNotificationEnabledClicked: () -> Unit,
    onNotificationDisabledClicked: () -> Unit
) {
    val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        null
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.ic_notifications),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(id = R.string.contentDescription_notification_icon)
        )
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (notificationsPermissionState?.status?.isGranted == false) {
                        notificationsPermissionState.launchPermissionRequest()
                    }
                }
                onNotificationEnabledClicked()
            }
        ) {
            if (areNotificationsEnabled) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = stringResource(id = R.string.notification_enable))
        }

        Button(
            onClick = { onNotificationDisabledClicked() }
        ) {
            if (!areNotificationsEnabled) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = stringResource(id = R.string.notification_disable))
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    HexagonalGamesTheme {
        SettingsScreen(
            onBackClick = {},
            areNotificationsEnabled = true,
            onNotificationEnabledClicked = {},
            onNotificationDisabledClicked = {}
        )
    }
}
