package com.example.quotevault.data.local

import androidx.room.TypeConverter
import com.example.quotevault.data.model.QuoteCategory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromQuoteCategory(category: QuoteCategory): String {
        return category.name
    }

    @TypeConverter
    fun toQuoteCategory(category: String): QuoteCategory {
        return QuoteCategory.valueOf(category)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toList(json: String): List<String> {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromLongList(list: List<Long>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toLongList(json: String): List<Long> {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
}