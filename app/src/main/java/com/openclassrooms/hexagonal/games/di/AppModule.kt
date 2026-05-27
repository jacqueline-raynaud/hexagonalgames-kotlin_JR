package com.openclassrooms.hexagonal.games.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.data.repository.PostRepositoryImpl
import com.openclassrooms.hexagonal.games.data.repository.StorageRepositoryImpl
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.data.service.PostFirestoreApi
import com.openclassrooms.hexagonal.games.domain.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun provideFirestore() : FirebaseFirestore = FirebaseFirestore.getInstance()

  @Provides
  @Singleton
  fun provideFirebaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()


  @Provides
  @Singleton
  fun providePostApi(firestore: FirebaseFirestore): PostApi {
    return PostFirestoreApi(firestore)
  }

  @Provides
  @Singleton
  fun providePostRepository(postApi: PostApi): PostRepository {
    return PostRepositoryImpl(postApi)
  }

  @Provides
  @Singleton
  fun provideFirebaseStorage(): FirebaseStorage =
    FirebaseStorage.getInstance()

  @Provides
  @Singleton
  fun provideStorageRepository(
    storage: FirebaseStorage,
    @ApplicationContext context: Context
  ): StorageRepository = StorageRepositoryImpl(storage, context)

}
