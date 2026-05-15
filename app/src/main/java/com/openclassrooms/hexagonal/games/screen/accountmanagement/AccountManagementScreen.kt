package com.openclassrooms.hexagonal.games.screen.accountmanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountManagementViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.accountmanagement)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() })
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        AccountManagement(
            modifier = Modifier.padding(contentPadding),
            onSignOutClicked = {
                viewModel.signOut(onSuccess=onBackClick)
                               },
            onDeleteAccountClicked = {
                viewModel.deleteAccount(
                onSuccess = {viewModel.signOut(onBackClick)},
                onFailure = { exception ->
                    // TODO toast ou snackbar d'erreur voir si etat
                }
            ) }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun AccountManagement(
    modifier: Modifier = Modifier,
    onSignOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onSignOutClicked() }) {
            Text(stringResource(id = R.string.sign_out))
        }
        Button(onClick = { onDeleteAccountClicked() }) {
            Text(stringResource(id = R.string.delete_account))
        }
    }
}

@Preview
@Composable
private fun AccountManagementPreview() {
    HexagonalGamesTheme {
        AccountManagement(
            onSignOutClicked = { },
            onDeleteAccountClicked = { }
        )
    }
}