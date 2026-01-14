package com.example.quotevault.data.local.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.quotevault.data.local.FavoriteEntity
import com.example.quotevault.data.local.QuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    // Quotes
    @Upsert
    suspend fun upsertQuote(quote: QuoteEntity)

    @Upsert
    suspend fun upsertQuotes(quotes: List<QuoteEntity>)

    @Query("SELECT * FROM quotes ORDER BY createdAt DESC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE category = :category ORDER BY createdAt DESC")
    fun getQuotesByCategory(category: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE content LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchQuotes(query: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE isFeatured = 1 ORDER BY RANDOM() LIMIT 1")
    fun getDailyQuote(): Flow<QuoteEntity?>

    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    fun getQuoteById(quoteId: String): Flow<QuoteEntity?>
    // Favorites
    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND quoteId = :quoteId")
    suspend fun removeFavorite(userId: String, quoteId: String)

    @Query("SELECT quoteId FROM favorites WHERE userId = :userId")
    fun getUserFavorites(userId: String): Flow<List<String>>

    @Query("SELECT * FROM quotes WHERE id IN (SELECT quoteId FROM favorites WHERE userId = :userId)")
    fun getFavoriteQuotes(userId: String): Flow<List<QuoteEntity>>

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND quoteId = :quoteId")
    fun isFavorite(userId: String, quoteId: String): Flow<Int>
}