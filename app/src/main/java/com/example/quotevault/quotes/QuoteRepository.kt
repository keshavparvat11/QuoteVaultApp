package com.example.quotevault.quotes


import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {

    /* ---------- AUTH ---------- */
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, name: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun getCurrentUser(): Any?

    /* ---------- QUOTES ---------- */
    suspend fun getQuotes(): Result<List<Quote>>
    suspend fun getQuotesByCategory(category: QuoteCategory): Result<List<Quote>>
    suspend fun searchQuotes(query: String): Result<List<Quote>>
    suspend fun getQuoteOfTheDay(): Result<Quote?>

    /* ---------- FAVORITES ---------- */
    fun getUserFavorites(): Flow<List<String>>
    suspend fun addToFavorites(quoteId: String): Result<Unit>
    suspend fun getQuoteById(quoteId: String): Result<Quote?>
    suspend fun getFavoriteQuotes(): Result<List<Quote>>
    suspend fun removeFromFavorites(quoteId: String): Result<Unit>
}
