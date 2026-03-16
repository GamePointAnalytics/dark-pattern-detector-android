package com.safeweb.darkpatterndetector.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2979FF), // Electric Blue
    secondary = Color(0xFF00B0FF),
    tertiary = Color(0xFF7C4DFF),
    background = Color(0xFF000000), // Pure Black
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFCCCCCC),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2979FF), // Electric Blue
    secondary = Color(0xFF00B0FF),
    tertiary = Color(0xFF7C4DFF),
    background = Color(0xFF000000), // Force Black Background
    surface = Color(0xFF121212),
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFCCCCCC),
)

@Composable
fun DarkPatternDetectorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We'll use the DarkColorScheme for both to ensure the black background
    // but we can still keep the toggle logic if needed.
    // For now, let's just make sure both have the black background requested.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Since background is always black now, we want light status bars (white icons)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
