package com.example.quotevault.data.model



import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferences {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val NOTIFICATION_TIME_KEY = stringPreferencesKey("notification_time")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        private val ACCENT_COLOR_KEY = stringPreferencesKey("accent_color")

        private const val DEFAULT_THEME = "SYSTEM"
        private const val DEFAULT_NOTIFICATION_TIME = "09:00"
        private const val DEFAULT_FONT_SIZE = "MEDIUM"
        private const val DEFAULT_ACCENT_COLOR = "PURPLE"
    }

    override suspend fun setTheme(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    override  fun getTheme(): Flow<Theme> {
        return dataStore.data.map { preferences ->
            Theme.valueOf(preferences[THEME_KEY] ?: DEFAULT_THEME)
        }
    }
    override fun isNotificationsEnabled(): Flow<Boolean> =
        dataStore.data.map {
            it[NOTIFICATIONS_ENABLED] ?: true
        }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit {
            it[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    override suspend fun setNotificationTime(time: String) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME_KEY] = time
        }
    }

    override  fun getNotificationTime(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NOTIFICATION_TIME_KEY] ?: DEFAULT_NOTIFICATION_TIME
        }
    }

    override suspend fun setFontSize(fontSize: FontSize) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize.name
        }
    }

    override fun getFontSize(): Flow<FontSize> {
        return dataStore.data.map { preferences ->
            FontSize.valueOf(preferences[FONT_SIZE_KEY] ?: DEFAULT_FONT_SIZE)
        }
    }

    override suspend fun setAccentColor(accentColor: AccentColor) {
        dataStore.edit { preferences ->
            preferences[ACCENT_COLOR_KEY] = accentColor.name
        }
    }

    override fun getAccentColor(): Flow<AccentColor> {
        return dataStore.data.map { preferences ->
            AccentColor.valueOf(preferences[ACCENT_COLOR_KEY] ?: DEFAULT_ACCENT_COLOR)
        }
    }
    private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")

    override fun isNotificationEnabled(): Flow<Boolean> =
        dataStore.data.map { it[NOTIFICATION_ENABLED] ?: true }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit {
            it[NOTIFICATION_ENABLED] = enabled
        }
    }
}