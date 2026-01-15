package com.example.quotevault.data.local



import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory

@Entity(tableName = "quotes")
@TypeConverters(Converters::class)
data class QuoteEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val author: String,
    val category: String, // Store as String
    val tags: List<String>,
    val likes: Int,
    val isFeatured: Boolean,
    val createdAt: Long,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromDomain(quote: Quote): QuoteEntity {
            return QuoteEntity(
                id = quote.id,
                content = quote.content,
                author = quote.author,
                category = quote.category.name, // Convert enum to string
                tags = quote.tags,
                likes = quote.likes,
                isFeatured = quote.isFeatured,
                createdAt = quote.createdAt?.toDate()?.time ?: System.currentTimeMillis()
            )
        }
    }

    fun toDomain(): Quote {
        return Quote(
            id = id,
            content = content,
            author = author,
            category = QuoteCategory.valueOf(category), // Convert string to enum
            tags = tags,
            likes = likes,
            isFeatured = isFeatured
        )
    }
}