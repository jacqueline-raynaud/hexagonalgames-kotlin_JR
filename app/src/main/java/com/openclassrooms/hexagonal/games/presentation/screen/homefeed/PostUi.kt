package com.openclassrooms.hexagonal.games.presentation.screen.homefeed

import androidx.compose.runtime.Immutable
import com.openclassrooms.hexagonal.games.domain.model.Post

@Immutable
data class PostUi(
    val id: String,
    val authorName: String,     // post.author?.nameUser ?: ""
    val title: String,
    val description: String?,
    val photoUrl: String?,
)

fun Post.toPostUi() = PostUi(
    id = id,
    authorName = author?.nameUser ?: "",
    title = title,
    description = description,
    photoUrl = photoUrl,
)