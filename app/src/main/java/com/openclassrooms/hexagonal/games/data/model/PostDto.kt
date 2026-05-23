package com.openclassrooms.hexagonal.games.data.model

import com.openclassrooms.hexagonal.games.domain.model.Post

data class PostDto(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val photoUrl: String? = null,
    val timestamp: Long = 0,
    val author: UserDto? = null
)
{
    fun toDomain() = Post(
    id = id,
    title = title,
    description = description,
    photoUrl = photoUrl,
    timestamp = timestamp,
    author = author?.toDomain()
)
}
