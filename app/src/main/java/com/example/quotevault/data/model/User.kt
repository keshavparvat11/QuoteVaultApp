package com.example.quotevault.data.model


data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val favorites: List<String> = emptyList(),
    val collections: List<String> = emptyList(),
    val settings: UserSettings = UserSettings()
)

data class UserSettings(
    val theme: Theme = Theme.SYSTEM,
    val notificationTime: String = "09:00",
    val fontSize: FontSize = FontSize.MEDIUM,
    val accentColor: AccentColor = AccentColor.PURPLE
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}

enum class FontSize {
    SMALL, MEDIUM, LARGE, XLARGE
}

enum class AccentColor(
    val colorHex: String
) {
    PURPLE("#7C3AED"),
    BLUE("#3B82F6"),
    GREEN("#10B981"),
    ORANGE("#F59E0B"),
    RED("#EF4444")
}