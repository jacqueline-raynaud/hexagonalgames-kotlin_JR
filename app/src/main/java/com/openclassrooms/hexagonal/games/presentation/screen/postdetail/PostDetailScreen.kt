package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.PostUi
import com.openclassrooms.hexagonal.games.presentation.screen.postdetail.comments.FirebaseUiCommentsList
import com.openclassrooms.hexagonal.games.presentation.ui.components.AppStateErrorDialog
import com.openclassrooms.hexagonal.games.presentation.ui.theme.HexagonalGamesTheme

@Composable
fun PostDetailScreen(
    postId: String,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by viewModel.post.collectAsStateWithLifecycle()
    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        viewModel.fetchPost(postId)
    }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            onBackClick()
        }
    }

    PostDetailScreen(
        postId = postId,
        post = post,
        appState = appState,
        uiState = uiState,
        onBackClick = onBackClick,
        onNavigateToLogin = onNavigateToLogin,
        onDeletePost = { viewModel.deletePost(postId) },
        onResetDeleteState = { viewModel.resetDeleteState() },
        onAddComment = { content -> viewModel.addComment(postId, content) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailScreen(
    postId: String,
    post: PostUi?,
    appState: AppState,
    uiState: PostDetailUiState,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onDeletePost: () -> Unit,
    onResetDeleteState: () -> Unit,
    onAddComment: (String) -> Unit,
) {
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showErrorDialog) {
        AppStateErrorDialog(
            appStatus = appState,
            onDismiss = { showErrorDialog = false },
            onNavigateToLogin = {
                showErrorDialog = false
                onNavigateToLogin()
            }
        )
    }

    if (uiState.deleteError != null) {
        AlertDialog(
            onDismissRequest = { onResetDeleteState() },
            title = { Text("Erreur") },
            text = { Text(uiState.deleteError) },
            confirmButton = {
                Button(onClick = { onResetDeleteState() }) {
                    Text("OK")
                }
            }
        )
    }

    if (showDeleteConfirmation && post != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Supprimer le post") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce post et tous ses commentaires ? Cette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeletePost()
                    },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.post_detail_label)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.contentDescription_go_back)
                        )
                    }
                },
                actions = {
                    if (post != null && post.authorId == uiState.currentUserId) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Supprimer le post",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        if (post == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        } else {
            PostDetailContent(
                modifier = Modifier.padding(contentPadding),
                postId = postId,
                post = post,
                onAddComment = { content ->
                    if (appState == AppState.Ready) {
                        onAddComment(content)
                    } else {
                        showErrorDialog = true
                    }
                }
            )
        }
    }
}

@Composable
fun PostDetailContent(
    modifier: Modifier = Modifier,
    postId: String,
    post: PostUi,
    onAddComment: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        // En-tête : le post + le titre "Commentaires"
        Column(
            modifier = Modifier
                //.weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PostInfo(post = post)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.comments_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider()

        // Liste de commentaires via FirebaseUI Firestore
        if (LocalInspectionMode.current) {
            Box(
                modifier = Modifier.weight(1f).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Comments Placeholder")
            }
        } else {
            FirebaseUiCommentsList(
                postId = postId,
                modifier = Modifier.weight(1f)
            )
        }

        // Barre de saisie
        CommentInput(onSendClick = onAddComment)
    }
}


@Composable
fun PostInfo(post: PostUi) {
    Column {
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.by, post.authorName),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        if (!post.photoUrl.isNullOrEmpty()) {
            AsyncImage(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .aspectRatio(16 / 9f),
                model = post.photoUrl,
                imageLoader = LocalContext.current.imageLoader.newBuilder()
                    .logger(DebugLogger())
                    .build(),
                placeholder = ColorPainter(Color.DarkGray),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        post.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun CommentInput(
    onSendClick: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.hint_comment)) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendClick(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.action_send)
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PostDetailScreenPreview() {
    HexagonalGamesTheme {
        PostDetailScreen(
            postId = "1",
            post = PostUi(
                id = "1",
                authorId = "01",
                authorName = "firstname lastname",
                title = "title",
                description = "description",
                photoUrl = null
            ),
            appState = AppState.Ready,
            uiState = PostDetailUiState(currentUserId = "01"),
            onBackClick = {},
            onNavigateToLogin = {},
            onDeletePost = {},
            onResetDeleteState = {},
            onAddComment = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PostDetailScreenLoadingPreview() {
    HexagonalGamesTheme {
        PostDetailScreen(
            postId = "1",
            post = null,
            appState = AppState.Ready,
            uiState = PostDetailUiState(),
            onBackClick = {},
            onNavigateToLogin = {},
            onDeletePost = {},
            onResetDeleteState = {},
            onAddComment = {}
        )
    }
}
