package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

/**
 * This class represents a Comment data object.
 */
data class Comment(
    val id: String = "",
    val postId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val author: User? = null
) : Serializable
