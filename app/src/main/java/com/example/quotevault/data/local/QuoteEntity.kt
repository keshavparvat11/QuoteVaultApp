package com.example.quotevault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.quotevault.data.model.Quote

@Entity(tableName = "quotes")
@TypeConverters(Converters::class)
data class QuoteEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val author: String,

    // Store category exactly as Supabase (STRING)
    val category: String,

    // Nullable to match domain model
    val tags: List<String>?,

    val likes: Int,
    val isFeatured: Boolean,

    // Store ISO date string (Supabase style)
    val createdAt: String,

    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {

        fun fromDomain(quote: Quote): QuoteEntity {
            return QuoteEntity(
                id = quote.id,
                content = quote.content,
                author = quote.author,
                category = quote.category,
                tags = quote.tags,
                likes = quote.likes,
                isFeatured = quote.isFeatured,
                createdAt = quote.createdAt
            )
        }
    }

    fun toDomain(): Quote {
        return Quote(
            id = id,
            content = content,
            author = author,
            category = category,
            tags = tags,
            likes = likes,
            isFeatured = isFeatured,
            createdAt = createdAt
        )
    }
}
