package com.example.quotevault.data.model
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

data class Quote(
    val id: String = "",
    val content: String = "",
    val author: String = "",
    val category: QuoteCategory = QuoteCategory.MOTIVATION,
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isFeatured: Boolean = false
) {
    companion object {
        val empty = Quote()
    }
}

enum class QuoteCategory(
    val displayName: String,
    val emoji: String
) {
    MOTIVATION("Motivation", "🚀"),
    LOVE("Love", "❤️"),
    SUCCESS("Success", "🏆"),
    WISDOM("Wisdom", "🧠"),
    HUMOR("Humor", "😄"),
    LIFE("Life", "🌱"),
    INSPIRATION("Inspiration", "✨"),
    BUSINESS("Business", "💼"),
    SPIRITUAL("Spiritual", "🙏"),
    FRIENDSHIP("Friendship", "👫");

    companion object {
        fun fromString(value: String): QuoteCategory {
            return try {
                QuoteCategory.valueOf(value.uppercase())
            } catch (e: Exception) {
                MOTIVATION
            }
        }
    }
}