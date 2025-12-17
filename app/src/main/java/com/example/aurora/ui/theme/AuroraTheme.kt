package com.example.aurora.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepNightBlue = Color(0xFF0D1B2A)
val VioletGlow = Color(0xFF9D5CFF)
val AuroraGreen = Color(0xFF3DDC97)
val SkyBlue = Color(0xFF61A5C2)
val SoftWhite = Color(0xFFF5F5F5)
val SoftBlue = Color(0xFFACE7F8)
val DarkLavender = Color(0xFF6B4897)
val LemonYellow = Color(0xFFFEEE91)
val AuroraPurple = Color(0xFF7B5CFF)

private val DarkColorScheme = darkColorScheme(
    primary = AuroraGreen,
    secondary = VioletGlow,
    tertiary = SoftBlue,
    background = DeepNightBlue,
    surface = Color(0xFF1B2838),
    onPrimary = DeepNightBlue,
    onSecondary = SoftWhite,
    onTertiary = DeepNightBlue,
    onBackground = SoftWhite,
    onSurface = SoftWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DarkLavender,
    secondary = SkyBlue,
    tertiary = AuroraGreen,
    background = SoftWhite,
    surface = Color.White,
    onPrimary = SoftWhite,
    onSecondary = DeepNightBlue,
    onTertiary = DeepNightBlue,
    onBackground = DeepNightBlue,
    onSurface = DeepNightBlue
)

@Composable
fun AuroraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
