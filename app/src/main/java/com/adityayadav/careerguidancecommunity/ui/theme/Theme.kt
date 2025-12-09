// Theme.kt
package com.adityayadav.careerguidancecommunity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.adityayadav.careerguidancecommunity.data.DataStoreManager

// You already have these colors defined in Color.kt
// md_theme_light_* and md_theme_dark_*.

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onBackground,
    error = md_theme_light_error
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onBackground,
    error = md_theme_dark_error
)

/**
 * Global app theme.
 *
 * Reads "theme_mode" from DataStore:
 *  - "light"  → always light
 *  - "dark"   → always dark
 *  - "system" (or anything else) → follow system
 */
@Composable
fun CareerGuidanceCommunityTheme(
    // Helpful for previews/tests if you want to force a variant
    forceDark: Boolean? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemIsDark = isSystemInDarkTheme()

    val storedMode by DataStoreManager
        .getStringFlow(context, "theme_mode", "system")
        .collectAsState(initial = "system")

    val useDark = remember(storedMode, forceDark, systemIsDark) {
        when {
            forceDark != null -> forceDark
            storedMode.equals("light", ignoreCase = true) -> false
            storedMode.equals("dark", ignoreCase = true) -> true
            else -> systemIsDark // "system" or anything unexpected
        }
    }

    val colors = if (useDark) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}