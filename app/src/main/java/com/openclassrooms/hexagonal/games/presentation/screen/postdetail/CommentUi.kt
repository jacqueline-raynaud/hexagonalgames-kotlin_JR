package com.openclassrooms.hexagonal.games.presentation.screen.postdetail

import androidx.compose.runtime.Immutable
import com.openclassrooms.hexagonal.games.domain.model.Comment

@Immutable
data class CommentUi(
    val id: String,
    val authorName: String,
    val content: String,
)

fun Comment.toCommentUi() = CommentUi(
    id = id,
    authorName = author?.nameUser ?: "Utilisateur",
    content = content,
)