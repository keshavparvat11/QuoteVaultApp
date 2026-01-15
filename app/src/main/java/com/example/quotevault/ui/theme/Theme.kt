package com.example.quotevault.ui.theme



import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import com.example.quotevault.data.model.AccentColor
import com.example.quotevault.data.model.FontSize
import com.example.quotevault.data.model.Theme


private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF333333),
    onSurface = Color(0xFF333333)
)

@Composable
fun QuoteVaultTheme(
    theme: Theme,
    fontSize: FontSize,
    accentColor: AccentColor,
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when (accentColor) {
        AccentColor.PURPLE ->
            if (darkTheme) darkColorScheme() else lightColorScheme()

        AccentColor.BLUE ->
            if (darkTheme) darkColorScheme(
                primary = Color(0xFF2196F3)
            ) else lightColorScheme(
                primary = Color(0xFF2196F3)
            )

        AccentColor.GREEN ->
            if (darkTheme) darkColorScheme(
                primary = Color(0xFF4CAF50)
            ) else lightColorScheme(
                primary = Color(0xFF4CAF50)
            )

        AccentColor.ORANGE ->
            if (darkTheme) darkColorScheme(
                primary = Color(0xFFFF9800)
            ) else lightColorScheme(
                primary = Color(0xFFFF9800)
            )

        AccentColor.RED ->
            if (darkTheme) darkColorScheme(
                primary = Color(0xFFF44336)
            ) else lightColorScheme(
                primary = Color(0xFFF44336)
            )
    }
    val baseTypography = Typography()

    val scaledTypography = when (fontSize) {
        FontSize.SMALL -> baseTypography.scale(0.9f)
        FontSize.MEDIUM -> baseTypography
        FontSize.LARGE -> baseTypography.scale(1.15f)
        FontSize.XLARGE -> baseTypography.scale(1.3f)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}



fun Typography.scale(factor: Float): Typography =
    Typography(
        displayLarge = displayLarge.scale(factor),
        displayMedium = displayMedium.scale(factor),
        displaySmall = displaySmall.scale(factor),
        headlineLarge = headlineLarge.scale(factor),
        headlineMedium = headlineMedium.scale(factor),
        headlineSmall = headlineSmall.scale(factor),
        titleLarge = titleLarge.scale(factor),
        titleMedium = titleMedium.scale(factor),
        titleSmall = titleSmall.scale(factor),
        bodyLarge = bodyLarge.scale(factor),
        bodyMedium = bodyMedium.scale(factor),
        bodySmall = bodySmall.scale(factor),
        labelLarge = labelLarge.scale(factor),
        labelMedium = labelMedium.scale(factor),
        labelSmall = labelSmall.scale(factor)
    )

private fun TextStyle.scale(factor: Float): TextStyle =
    if (fontSize.isUnspecified) this else copy(fontSize = fontSize * factor)