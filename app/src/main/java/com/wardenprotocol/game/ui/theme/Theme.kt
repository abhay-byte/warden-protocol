package com.wardenprotocol.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VaultGreen,
    secondary = AccentGreen,
    tertiary = WarningAmber,
    background = BackgroundBlack,
    surface = SurfaceBlack,
    error = DangerRed,
    onPrimary = BackgroundBlack,
    onSecondary = BackgroundBlack,
    onTertiary = BackgroundBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = BackgroundBlack
)

@Composable
fun WardenProtocolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
