package com.openclassrooms.hexagonal.games.presentation.screen.ad

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.presentation.ui.components.AppStateErrorDialog
import com.openclassrooms.hexagonal.games.presentation.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    val viewModel: AddViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appState by viewModel.appState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaveClick()
        }
    }

    if (uiState.showAuthError && appState !is AppState.Ready) {
        AppStateErrorDialog(
            appStatus = appState,
            onDismiss = { viewModel.dismissAuthError() },
            onNavigateToLogin = onNavigateToLogin
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_fragment_label))
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

        CreatePost(
            modifier = Modifier.padding(contentPadding),
            uiState = uiState,
            onAction = { viewModel.onAction(it) }
        )
    }
}

@Composable
private fun CreatePost(
    modifier: Modifier = Modifier,
    uiState: AddUiState,
    onAction: (FormEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onAction(FormEvent.ImageSelected(uri))
            }
        })

    Column(
        modifier = modifier
          .padding(16.dp)
          .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
              .fillMaxSize()
              .weight(1f)
              .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                modifier = Modifier
                  .padding(top = 16.dp)
                  .fillMaxWidth(),
                value = uiState.title,
                isError = uiState.error is FormError.TitleMissing,
                onValueChange = { onAction(FormEvent.TitleChanged(it)) },
                label = { Text(stringResource(id = R.string.hint_title)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
            if (uiState.error != null) {
                Text(
                    text = stringResource(id = uiState.error.messageRes),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            OutlinedTextField(
                modifier = Modifier
                  .padding(top = 16.dp)
                  .fillMaxWidth(),
                value = uiState.description,
                onValueChange = { onAction(FormEvent.DescriptionChanged(it)) },
                label = { Text(stringResource(id = R.string.hint_description)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                Text(text = "Choisir une image")
            }
        }
        Button(
            enabled = !uiState.isSaving,
            onClick = { onAction(FormEvent.SaveClicked) }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.action_save)
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostPreview() {
    HexagonalGamesTheme {
        CreatePost(
            uiState = AddUiState(
                title = "Test de titre",
                description = "Une petite description sympa"
            ),
            onAction = { }
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostErrorPreview() {
    HexagonalGamesTheme {
        CreatePost(
            uiState = AddUiState(
                title = "",
                error = FormError.TitleMissing
            ),
            onAction = { }
        )
    }
}
