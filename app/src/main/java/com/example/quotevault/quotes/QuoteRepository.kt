package com.example.quotevault.quotes


import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.model.User
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    // Authentication
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun getCurrentUser(): User?
    fun getAuthState(): Flow<AuthState>

    // Quotes
    suspend fun getQuotes(page: Int = 1, limit: Int = 20): Result<List<Quote>>
    suspend fun searchQuotes(query: String): Result<List<Quote>>
    suspend fun getQuotesByCategory(category: QuoteCategory): Result<List<Quote>>
    suspend fun getQuoteOfTheDay(): Result<Quote>
    suspend fun getQuoteById(quoteId: String): Result<Quote>

    // Favorites
    suspend fun addToFavorites(quoteId: String): Result<Unit>
    suspend fun removeFromFavorites(quoteId: String): Result<Unit>
    fun getUserFavorites(): Flow<List<String>>
    suspend fun getFavoriteQuotes(): Result<List<Quote>>

    // Collections
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

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    object PasswordResetSent : AuthState()
}

data class Collection(
    val id: String,
    val name: String,
    val description: String,
    val userId: String,
    val quoteIds: List<String>,
    val isPublic: Boolean,
    val createdAt: Long,
    val coverImageUrl: String? = null
)

data class UserSettings(
    val theme: Theme = Theme.SYSTEM,
    val notificationTime: String = "09:00",
    val fontSize: FontSize = FontSize.MEDIUM,
    val accentColor: AccentColor = AccentColor.PURPLE
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}

enum class FontSize {
    SMALL, MEDIUM, LARGE, XLARGE
}

enum class AccentColor(val colorHex: String) {
    PURPLE("#7C3AED"),
    BLUE("#3B82F6"),
    GREEN("#10B981"),
    ORANGE("#F59E0B"),
    RED("#EF4444")
}
