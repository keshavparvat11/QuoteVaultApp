package com.example.quotevault.data.model


import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun setTheme(theme: Theme)
    suspend fun getTheme(): Flow<Theme>

    suspend fun setNotificationTime(time: String)
    suspend fun getNotificationTime(): Flow<String>

    suspend fun setFontSize(fontSize: FontSize)
    suspend fun getFontSize(): Flow<FontSize>
    fun isNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAccentColor(accentColor: AccentColor)
    suspend fun getAccentColor(): Flow<AccentColor>
}