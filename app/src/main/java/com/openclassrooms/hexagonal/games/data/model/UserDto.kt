package com.openclassrooms.hexagonal.games.data.model

import com.openclassrooms.hexagonal.games.domain.model.User

data class UserDto(
    val id: String = "",
    val nameUser: String = ""
) {
    fun toDomain() = User(
        id = id,
        nameUser = nameUser
    )
}
