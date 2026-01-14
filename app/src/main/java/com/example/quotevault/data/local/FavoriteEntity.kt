package com.example.quotevault.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", primaryKeys = ["userId", "quoteId"])
data class FavoriteEntity(
    val userId: String,
    val quoteId: String,
    val addedAt: Long = System.currentTimeMillis()
)