package com.openclassrooms.hexagonal.games.domain.repository

import android.net.Uri

interface StorageRepository {
    suspend fun uploadImage(userId: String, uri: Uri): String
}