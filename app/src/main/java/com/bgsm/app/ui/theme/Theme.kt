package com.bgsm.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BgsmDarkColorScheme = darkColorScheme(
    primary = BgsmAccentLime,
    onPrimary = BgsmBackground,
    primaryContainer = BgsmSurfaceElevated,
    onPrimaryContainer = BgsmAccentLime,
    secondary = BgsmAccentCyan,
    onSecondary = BgsmBackground,
    secondaryContainer = BgsmSurfaceElevated,
    onSecondaryContainer = BgsmAccentCyan,
    tertiary = BgsmAccentOrange,
    onTertiary = BgsmBackground,
    background = BgsmBackground,
    onBackground = BgsmTextPrimary,
    surface = BgsmSurface,
    onSurface = BgsmTextPrimary,
    surfaceVariant = BgsmSurfaceElevated,
    onSurfaceVariant = BgsmTextSecondary,
    outline = BgsmBorder,
    error = BgsmError,
    onError = BgsmBackground
)

@Composable
fun BgsmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // BGSM strictly adheres to the Elegant Dark gaming theme
    MaterialTheme(
        colorScheme = BgsmDarkColorScheme,
        typography = Typography,
        content = content
    )
}
