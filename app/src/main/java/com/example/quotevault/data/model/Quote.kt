package com.example.quotevault.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: String = "",
    val content: String = "",
    val author: String = "",
    val category: String = "MOTIVATION",
    val tags: List<String>? = emptyList(),
    val likes: Int = 0,
    @SerialName("is_featured")
    val isFeatured: Boolean = false,
    @SerialName("created_at")
    val createdAt: String = ""
)


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