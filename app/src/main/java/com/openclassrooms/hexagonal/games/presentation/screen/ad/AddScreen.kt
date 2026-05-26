package com.openclassrooms.hexagonal.games.presentation.screen.ad

import android.net.Uri
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.presentation.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
  modifier: Modifier = Modifier,
  viewModel: AddViewModel = hiltViewModel(),
  onBackClick: () -> Unit,
  onSaveClick: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  // gestion de navigation que si post a eu le temps d'être créé
  LaunchedEffect(uiState.isSaved) {
    if (uiState.isSaved) {
      onSaveClick()
    }
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
      error = uiState.error,
      title = uiState.title,
      onTitleChanged = { viewModel.onAction(FormEvent.TitleChanged(it)) },
      description = uiState.description ?: "",
      onDescriptionChanged = { viewModel.onAction(FormEvent.DescriptionChanged(it)) },
      onImageSelected = { uri -> viewModel.onAction(FormEvent.ImageSelected(uri)) },
      onSaveClicked = {
        viewModel.addPost()
/*      pbl avec image pas suffisament de temps cancel immédiat
        si je lance onSave tout de suite
        l'image va dans storage mais le post n'a pas le temps d'être créé
        ajouter un issaved true dans viewmodel et une navigtation launchedeffect en haut
        onSaveClick()*/
      }
    )
  }
}

@Composable
private fun CreatePost(
  modifier: Modifier = Modifier,
  title: String,
  onTitleChanged: (String) -> Unit,
  description: String,
  onDescriptionChanged: (String) -> Unit,
  onImageSelected: (Uri) -> Unit,
  onSaveClicked: () -> Unit,
  error: FormError?
) {
  val scrollState = rememberScrollState()
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        onImageSelected(uri)
      }
      })

  Column(
    modifier = modifier
      .padding(16.dp)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = modifier
        .fillMaxSize()
        .weight(1f)
        .verticalScroll(scrollState)
    ) {
      OutlinedTextField(
        modifier = Modifier
          .padding(top = 16.dp)
          .fillMaxWidth(),
        value = title,
        isError = error is FormError.TitleError,
        onValueChange = { onTitleChanged(it) },
        label = { Text(stringResource(id = R.string.hint_title)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true
      )
      if (error is FormError.TitleError) {
        Text(
          text = stringResource(id = error.messageRes),
          color = MaterialTheme.colorScheme.error,
        )
      }
      OutlinedTextField(
        modifier = Modifier
          .padding(top = 16.dp)
          .fillMaxWidth(),
        value = description,
        onValueChange = { onDescriptionChanged(it) },
        label = { Text(stringResource(id = R.string.hint_description)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
      )
     Button(onClick = {
      launcher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
      )
      }) {
        Text(text = "Choisir une image")


    }
    }
    Button(
      enabled = error == null,
      onClick = { onSaveClicked() }
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
      title = "test",
      onTitleChanged = { },
      description = "description",
      onDescriptionChanged = { },
      onSaveClicked = { },
      onImageSelected = { },

      error = null
    )
  }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostErrorPreview() {
  HexagonalGamesTheme {
    CreatePost(
      title = "test",
      onTitleChanged = { },
      description = "description",
      onDescriptionChanged = { },
      onSaveClicked = { },
      onImageSelected = { },
      error = FormError.TitleError
    )
  }
}