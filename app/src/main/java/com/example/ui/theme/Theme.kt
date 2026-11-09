package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MonochromeDarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = ElevatedDark,
    onPrimaryContainer = PureWhite,
    secondary = GrayLight,
    onSecondary = PureBlack,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = PureWhite,
    tertiary = GrayMuted,
    onTertiary = PureBlack,
    background = BackgroundDark,
    onBackground = OffWhite,
    surface = SurfaceDark,
    onSurface = OffWhite,
    surfaceVariant = ElevatedDark,
    onSurfaceVariant = GrayMuted,
    outline = BorderDark,
    outlineVariant = BorderLight,
    error = PureWhite,
    onError = PureBlack
)

@Composable
fun AhaduTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MonochromeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
