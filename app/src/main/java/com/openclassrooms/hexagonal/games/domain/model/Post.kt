package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

/**
 * This class represents a Post data object. It holds information about a post, including its
 * ID, title, description, photo URL, creation timestamp, and the author (User object).
 * The class implements Serializable to allow for potential serialization needs.
 */
data class Post(
    val id: String,
    val title: String,
    val description: String?,
    val photoUrl: String?,
    val timestamp: Long,
    val author: User?
) : Serializable
