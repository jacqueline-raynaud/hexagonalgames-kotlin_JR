package com.openclassrooms.hexagonal.games.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : StorageRepository {

    override suspend fun uploadImage(userId: String, uri: Uri): String {
        // Chemin conforme aux règles Storage : posts/{userId}/{fileName}
        val fileName = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child("posts/$userId/$fileName")

        // Lecture du contenu de l'URI (nécessite le Context)
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Impossible de lire l'URI : $uri")

        val bytes = inputStream.use { it.readBytes() }

        // Upload et attente du résultat (suspending)
        ref.putBytes(bytes).await()

        // Récupération de l'URL de téléchargement publique
        return ref.downloadUrl.await().toString()
    }
}