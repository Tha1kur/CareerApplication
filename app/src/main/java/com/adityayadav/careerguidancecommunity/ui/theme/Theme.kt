package com.adityayadav.careerguidancecommunity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light colors
private val LightColors = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
    onPrimary = Color.White
)

// Dark colors
private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    onPrimary = Color.Black
)

@Composable
fun CareerGuidanceCommunityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
