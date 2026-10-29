package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SakoonPrimary,
    onPrimary = Color.White,
    primaryContainer = SakoonPrimaryContainer,
    onPrimaryContainer = SakoonOnPrimaryContainer,
    secondary = SakoonGreen,
    onSecondary = Color.White,
    secondaryContainer = SakoonGreenBg,
    onSecondaryContainer = SakoonGreen,
    tertiary = SakoonRose,
    onTertiary = Color.White,
    tertiaryContainer = SakoonRoseBg,
    onTertiaryContainer = SakoonRose,
    background = SakoonBackground,
    onBackground = SakoonTextPrimary,
    surface = SakoonSurface,
    onSurface = SakoonTextPrimary,
    surfaceVariant = SakoonSurfaceVariant,
    onSurfaceVariant = SakoonTextSecondary,
    outline = SakoonOutline,
    outlineVariant = SakoonOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF1E0E45),
    primaryContainer = Color(0xFF3B1A75),
    onPrimaryContainer = Color(0xFFEDE5FF),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFFFB7185),
    onTertiary = Color(0xFF4C0519),
    tertiaryContainer = Color(0xFF881337),
    onTertiaryContainer = Color(0xFFFECDD3),
    background = Color(0xFF0B0F19),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131827),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E2638),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
