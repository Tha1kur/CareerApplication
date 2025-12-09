// Shapes.kt
package com.adityayadav.careerguidancecommunity.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Global shape system for cards, chips, buttons, etc.
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // chips, small pills
    small      = RoundedCornerShape(12.dp),  // small cards, text fields
    medium     = RoundedCornerShape(16.dp),  // main cards, buttons
    large      = RoundedCornerShape(24.dp),  // big surfaces, sheets
    extraLarge = RoundedCornerShape(32.dp)   // full‑width hero areas
)