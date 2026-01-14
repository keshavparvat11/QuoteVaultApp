package com.example.quotevault.data.model

data class Collection(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val userId: String = "",
    val quoteIds: List<String> = emptyList(),
    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val coverImageUrl: String? = null
)
