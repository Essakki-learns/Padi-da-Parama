package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
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
    alpha: Float = 0.88f
): Modifier {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0xEE1E293B),
                Color(0xCC0B1120)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = alpha),
                Color(0xF5F8FAFC).copy(alpha = alpha * 0.85f)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0x55475569),
                Color(0x22334155)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color(0xAAFFFFFF),
                Color(0x44CBD5E1)
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
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF0B1120),
                Color(0xFF0F172A),
                Color(0xFF13112E),
                Color(0xFF0B1120)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF8FAFC),
                Color(0xFFEEF2FF),
                Color(0xFFE8F0FE),
                Color(0xFFF5F3FF)
            )
        )
    }

    return this.background(backgroundBrush)
}

/**
 * Explicit color mapping for all text input fields to ensure typed text
 * is crisp white in Dark Mode and crisp dark-slate in Light Mode.
 */
@Composable
fun appTextFieldColors(): TextFieldColors {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val textCol = if (isDark) Color.White else Color(0xFF0F172A)
    val labelCol = if (isDark) Color(0xFFA5B4FC) else PrimaryIndigo
    val mutedCol = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)
    val placeholderCol = if (isDark) Color(0xFF94A3B8) else Color(0xFF94A3B8)

    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = textCol,
        unfocusedTextColor = textCol,
        focusedLabelColor = labelCol,
        unfocusedLabelColor = mutedCol,
        focusedPlaceholderColor = placeholderCol,
        unfocusedPlaceholderColor = placeholderCol,
        focusedBorderColor = labelCol,
        unfocusedBorderColor = if (isDark) Color(0xFF64748B) else Color(0xFFCBD5E1),
        cursorColor = labelCol,
        focusedLeadingIconColor = labelCol,
        unfocusedLeadingIconColor = mutedCol,
        focusedTrailingIconColor = labelCol,
        unfocusedTrailingIconColor = mutedCol
    )
}

