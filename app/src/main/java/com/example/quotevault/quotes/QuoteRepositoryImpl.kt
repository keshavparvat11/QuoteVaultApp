package com.example.quotevault.quotes

import com.example.quotevault.data.local.FavoriteEntity
import com.example.quotevault.data.local.QuoteEntity
import com.example.quotevault.data.local.dao.QuoteDao
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.Quote.Companion.empty
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.model.User
import com.example.quotevault.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject


class QuoteRepositoryImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val quoteDao: QuoteDao
) : QuoteRepository {

    // Authentication
    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val user = firebaseRepository.signIn(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val user = firebaseRepository.signUp(email, password, name)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseRepository.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseRepository.resetPassword(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseRepository.getCurrentUser()
        return firebaseUser?.let { user ->
            try {
                firebaseRepository.getUser(user.uid)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun getAuthState(): Flow<AuthState> {
        return firebaseRepository.getAuthStateFlow().map { firebaseUser ->
            if (firebaseUser != null) {
                try {
                    val user = firebaseRepository.getUser(firebaseUser.uid)
                    AuthState.Authenticated(user)
                } catch (e: Exception) {
                    AuthState.Error("Failed to load user data")
                }
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    // Quotes
    override suspend fun getQuotes(page: Int, limit: Int): Result<List<Quote>> {
        return try {
            val quotes = firebaseRepository.getQuotes(limit)
            // Cache to local database
            val quoteEntities = quotes.map { quote ->
                QuoteEntity(
                    id = quote.id,
                    content = quote.content,
                    author = quote.author,
                    category = quote.category.name,
                    tags = quote.tags,
                    likes = quote.likes,
                    isFeatured = quote.isFeatured,
                    createdAt = System.currentTimeMillis()
                )
            }
            quoteDao.upsertQuotes(quoteEntities)
            Result.success(quotes)
        } catch (e: Exception) {
            // Fallback to local data
            try {
                val localQuotes = quoteDao.getAllQuotes()
                    .map { entities ->
                        entities.map { entity ->
                            convertEntityToQuote(entity)
                        }
                    }
                    .firstOrNull() ?: emptyList()
                Result.success(localQuotes)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }

    override suspend fun searchQuotes(query: String): Result<List<Quote>> {
        return try {
            val quotes = firebaseRepository.searchQuotes(query)
            Result.success(quotes)
        } catch (e: Exception) {
            // Fallback to local search
            try {
                val localQuotes = quoteDao.searchQuotes(query)
                    .map { entities ->
                        entities.map { entity ->
                            convertEntityToQuote(entity)
                        }
                    }
                    .firstOrNull() ?: emptyList()
                Result.success(localQuotes)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }

    override suspend fun getQuotesByCategory(category: QuoteCategory): Result<List<Quote>> {
        return try {
            val quotes = firebaseRepository.getQuotesByCategory(category)
            Result.success(quotes)
        } catch (e: Exception) {
            try {
                val localQuotes = quoteDao.getQuotesByCategory(category.name)
                    .map { entities ->
                        entities.map { entity ->
                            convertEntityToQuote(entity)
                        }
                    }
                    .firstOrNull() ?: emptyList()
                Result.success(localQuotes)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }

    override suspend fun getQuoteOfTheDay(): Result<Quote> {
        return try {
            val quote = firebaseRepository.getQuoteOfTheDay()
            Result.success(quote)
        } catch (e: Exception) {
            try {
                val localQuote = quoteDao.getDailyQuote()
                    .map { entity ->
                        entity?.let { convertEntityToQuote(it) }
                    }
                    .firstOrNull() ?: Quote.empty
                Result.success(localQuote)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }

    override suspend fun getQuoteById(quoteId: String): Result<Quote> {
        return try {
            // Get the Flow from DAO
            val quoteFlow = quoteDao.getQuoteById(quoteId)

            // Use firstOrNull() to get the first value from the Flow
            val entity = quoteFlow.firstOrNull()

            val quote = entity?.let {
                convertEntityToQuote(it)
            } ?: Quote.empty

            if (quote.id.isNotEmpty()) {
                Result.success(quote)
            } else {
                // Try Firebase as fallback
                try {
                    val firebaseQuote = firebaseRepository.getQuoteById(quoteId)
                    Result.success(firebaseQuote)
                } catch (e: Exception) {
                    Result.failure(Exception("Quote not found in local or remote storage"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Favorites
    override suspend fun addToFavorites(quoteId: String): Result<Unit> {
        val userId = firebaseRepository.getCurrentUser()?.uid
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            firebaseRepository.addToFavorites(userId, quoteId)
            quoteDao.upsertFavorite(
                FavoriteEntity(userId, quoteId)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(quoteId: String): Result<Unit> {
        val userId = firebaseRepository.getCurrentUser()?.uid
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            firebaseRepository.removeFromFavorites(userId, quoteId)
            quoteDao.removeFavorite(userId, quoteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserFavorites(): Flow<List<String>> {
        val userId = firebaseRepository.getCurrentUser()?.uid
            ?: return flowOf(emptyList())

        return quoteDao.getUserFavorites(userId)
    }

    override suspend fun getFavoriteQuotes(): Result<List<Quote>> {
        TODO("Not yet implemented")
    }

//    override suspend fun getFavoriteQuotes(): Result<List<Quote>> {
//        val userId = firebaseRepository.getCurrentUser()?.uid
//            ?: return Result.failure(Exception("User not logged in"))
//
//        return try {
//            val quotes = firebaseRepository.getFavoriteQuotes(userId)
//            Result.success(quotes)
//        } catch (e: Exception) {
//            try {
//                val localQuotes = quoteDao.getFavoriteQuotes(userId)
//                    .map { entities ->
//                        entities.map { entity ->
//                            convertEntityToQuote(entity)
//                        }
//                    }
//                    .firstOrNull() ?: emptyList()
//                Result.success(localQuotes)
//            } catch (e2: Exception) {
//                Result.failure(e2)
//            }
//        }
//    }

    // Collections
    override suspend fun createCollection(name: String, description: String): Result<String> {
        val userId = firebaseRepository.getCurrentUser()?.uid
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            val collectionId = firebaseRepository.createCollection(userId, name, description)
            Result.success(collectionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToCollection(collectionId: String, quoteId: String): Result<Unit> {
        val userId = firebaseRepository.getCurrentUser()?.uid
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            firebaseRepository.addToCollection(collectionId, userId, quoteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCollection(
        collectionId: String,
        quoteId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserCollections(): Result<List<Collection>> {
        TODO("Not yet implemented")
    }

//    override suspend fun removeFromCollection(collectionId: String, quoteId: String): Result<Unit> {
//        val userId = firebaseRepository.getCurrentUser()?.uid
//            ?: return Result.failure(Exception("User not logged in"))
//
//        return try {
//            firebaseRepository.removeFromCollection(collectionId, userId, quoteId)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

//    override suspend fun getUserCollections(): Result<List<Collection>> {
//        val userId = firebaseRepository.getCurrentUser()?.uid
//            ?: return Result.failure(Exception("User not logged in"))
//
//        return try {
//            val collections = firebaseRepository.getUserCollections(userId)
//            Result.success(collections)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    // User settings
    override suspend fun updateUserSettings(settings: UserSettings): Result<Unit> {
        val userId = firebaseRepository.getCurrentUser()?.uid
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            val updates = hashMapOf<String, Any>(
                "settings.theme" to settings.theme.name,
                "settings.notificationTime" to settings.notificationTime,
                "settings.fontSize" to settings.fontSize.name,
                "settings.accentColor" to settings.accentColor.name
            )

            firebaseRepository.updateUserSettings(userId, updates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Daily quote observation
    override fun observeDailyQuote(): Flow<Quote?> {
        return quoteDao.getDailyQuote().map { entity ->
            entity?.let { convertEntityToQuote(it) }
        }
    }

    // Data seeding
    override suspend fun seedQuotes() {
        // Implementation for seeding quotes from a local source
    }

    // Helper function to convert QuoteEntity to Quote
    private fun convertEntityToQuote(entity: QuoteEntity): Quote {
        return Quote(
            id = entity.id,
            content = entity.content,
            author = entity.author,
            category = try {
                QuoteCategory.valueOf(entity.category)
            } catch (e: Exception) {
                QuoteCategory.MOTIVATION
            },
            tags = entity.tags,
            likes = entity.likes,
            isFeatured = entity.isFeatured
        )
    }
}