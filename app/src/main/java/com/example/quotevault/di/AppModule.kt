package com.example.quotevault.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quotevault.data.local.QuoteDatabase
import com.example.quotevault.data.local.dao.QuoteDao
import com.example.quotevault.data.model.UserPreferences
import com.example.quotevault.data.model.UserPreferencesRepository
import com.example.quotevault.data.remote.FirebaseRepository
import com.example.quotevault.quotes.QuoteRepository
import com.example.quotevault.quotes.QuoteRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseRepository = FirebaseRepository(auth, firestore)

    @Provides
    @Singleton
    fun provideQuoteDatabase(@ApplicationContext context: Context): QuoteDatabase {
        return QuoteDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideQuoteDao(database: QuoteDatabase) = database.quoteDao()

    @Provides
    @Singleton
    fun provideQuoteRepository(
        firebaseRepository: FirebaseRepository,
        quoteDao: QuoteDao
    ): QuoteRepository {
        return QuoteRepositoryImpl(firebaseRepository, quoteDao)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferencesRepository(dataStore)
    }
}