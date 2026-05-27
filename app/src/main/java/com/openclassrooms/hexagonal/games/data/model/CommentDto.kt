package com.openclassrooms.hexagonal.games.data.model

import com.openclassrooms.hexagonal.games.domain.model.Comment

data class CommentDto(
    val id: String = "",
    val postId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val author: UserDto? = null
) {
    fun toDomain() = Comment(
        id = id,
        postId = postId,
        content = content,
        timestamp = timestamp,
        author = author?.toDomain()
    )
}
