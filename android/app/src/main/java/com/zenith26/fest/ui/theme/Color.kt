package com.zenith26.fest.ui.theme

import androidx.compose.ui.graphics.Color

val Slate950 = Color(0xFF020617)
val Slate900 = Color(0xFF0F172A)
val Slate850 = Color(0xFF141E33)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF1F5F9)
val PureWhite = Color(0xFFFFFFFF)

// Zenith '26 Vibrant Fest Brand Colors
val Purple600 = Color(0xFF7C3AED)
val Purple500 = Color(0xFF8B5CF6)
val Purple400 = Color(0xFFA78BFA)
val Purple300 = Color(0xFFC4B5FD)
val Purple950 = Color(0xFF2E1065)
val Purple900 = Color(0xFF4C1D95)

val Pink600 = Color(0xFFDB2777)
val Pink500 = Color(0xFFEC4899)
val Pink400 = Color(0xFFF472B6)
val Pink300 = Color(0xFFF9A8D4)

val Amber500 = Color(0xFFF59E0B)
val Amber400 = Color(0xFFFBBF24)
val Amber300 = Color(0xFFFCD34D)

val Emerald500 = Color(0xFF10B981)
val Emerald400 = Color(0xFF34D399)
val Emerald300 = Color(0xFF6EE7B7)

val Cyan500 = Color(0xFF06B6D4)
val Cyan400 = Color(0xFF22D3EE)
val Cyan300 = Color(0xFF67E8F9)

val Rose500 = Color(0xFFF43F5E)
val Rose400 = Color(0xFFFB7185)
val Rose300 = Color(0xFFFDA4AF)
val Rose950 = Color(0xFF4C0519)

data class CategoryTheme(
    val textColor: Color,
    val backgroundColor: Color,
    val borderColor: Color
)

fun getCategoryTheme(category: String?): CategoryTheme {
    return when ((category ?: "").lowercase()) {
        "technical" -> CategoryTheme(
            textColor = Cyan300,
            backgroundColor = Cyan500.copy(alpha = 0.15f),
            borderColor = Cyan500.copy(alpha = 0.35f)
        )
        "cultural" -> CategoryTheme(
            textColor = Pink300,
            backgroundColor = Pink500.copy(alpha = 0.15f),
            borderColor = Pink500.copy(alpha = 0.35f)
        )
        "sports" -> CategoryTheme(
            textColor = Emerald300,
            backgroundColor = Emerald500.copy(alpha = 0.15f),
            borderColor = Emerald500.copy(alpha = 0.35f)
        )
        "gaming" -> CategoryTheme(
            textColor = Purple300,
            backgroundColor = Purple500.copy(alpha = 0.15f),
            borderColor = Purple500.copy(alpha = 0.35f)
        )
        "workshop" -> CategoryTheme(
            textColor = Amber300,
            backgroundColor = Amber500.copy(alpha = 0.15f),
            borderColor = Amber500.copy(alpha = 0.35f)
        )
        else -> CategoryTheme(
            textColor = Slate300,
            backgroundColor = Slate800.copy(alpha = 0.6f),
            borderColor = Slate700
        )
    }
}
