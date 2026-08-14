package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable Frosted Glass styling modifier for containers, cards, and bars.
 */
@Composable
fun Modifier.frostedGlass(
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 2.dp,
    alpha: Float = 0.82f
): Modifier {
    val isDark = isSystemInDarkTheme()

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0x591E293B),
                Color(0x380F172A)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = alpha),
                Color(0xF0FFFFFF).copy(alpha = alpha * 0.75f)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.28f),
                Color.White.copy(alpha = 0.06f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.40f)
            )
        )
    }

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A4338CA),
            spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x146366F1)
        )
        .clip(shape)
        .background(backgroundBrush)
        .border(borderWidth, borderBrush, shape)
}

/**
 * Ambient background mesh gradient for the frosted glass theme.
 */
@Composable
fun Modifier.ambientBackground(): Modifier {
    val isDark = isSystemInDarkTheme()

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF090D16),
                Color(0xFF0F172A),
                Color(0xFF13112E),
                Color(0xFF090D16)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF4F7FB),
                Color(0xFFEEF2FF),
                Color(0xFFE8F0FE),
                Color(0xFFF5F3FF)
            )
        )
    }

    return this.background(backgroundBrush)
}
