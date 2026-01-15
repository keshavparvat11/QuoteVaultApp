package com.example.quotevault.quotes

import com.example.quotevault.data.model.Collection
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.model.User
import com.example.quotevault.data.model.UserSettings
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState

import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    // Authentication
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun getCurrentUser(): User?
    fun getAuthState(): Flow<com.example.quotevault.data.model.AuthState>

    // Quotes - ALL return Result<T>
    suspend fun getQuotes(page: Int = 1, limit: Int = 20): Result<List<Quote>>
    suspend fun searchQuotes(query: String): Result<List<Quote>>
    suspend fun getQuotesByCategory(category: QuoteCategory): Result<List<Quote>>
    suspend fun getQuoteOfTheDay(): Result<Quote>
    suspend fun getQuoteById(quoteId: String): Result<Quote>

    // Favorites - ALL return Result<T>
    suspend fun addToFavorites(quoteId: String): Result<Unit>
    suspend fun removeFromFavorites(quoteId: String): Result<Unit>
    fun getUserFavorites(): Flow<List<String>>
    suspend fun getFavoriteQuotes(): Result<List<Quote>>

    // Collections - ALL return Result<T>
    suspend fun createCollection(name: String, description: String = ""): Result<String>
    suspend fun addToCollection(collectionId: String, quoteId: String): Result<Unit>
    suspend fun removeFromCollection(collectionId: String, quoteId: String): Result<Unit>
    suspend fun getUserCollections(): Result<List<Collection>>

    // User preferences
    suspend fun updateUserSettings(settings: UserSettings): Result<Unit>

    // Daily quote
    fun observeDailyQuote(): Flow<Quote?>

    // Data seeding
    suspend fun seedQuotes()
}

// Rest of the interface remains the same...