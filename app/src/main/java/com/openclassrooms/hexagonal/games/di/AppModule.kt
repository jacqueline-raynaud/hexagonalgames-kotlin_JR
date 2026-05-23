package com.openclassrooms.hexagonal.games.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.data.service.PostFirestoreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}
