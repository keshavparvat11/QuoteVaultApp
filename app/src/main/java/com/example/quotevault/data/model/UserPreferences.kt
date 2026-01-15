package com.example.quotevault.data.model


import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun setTheme(theme: Theme)
     fun getTheme(): Flow<Theme>
    fun isNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun setNotificationTime(time: String)
     fun getNotificationTime(): Flow<String>

    suspend fun setFontSize(fontSize: FontSize)
     fun getFontSize(): Flow<FontSize>
    fun isNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAccentColor(accentColor: AccentColor)
     fun getAccentColor(): Flow<AccentColor>
}