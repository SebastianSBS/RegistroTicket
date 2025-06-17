package edu.ucne.registroticket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekDarkColors = darkColorScheme(
    primary = Color(0xFF82B1FF),
    onPrimary = Color(0xFF0D0D0D),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = Color(0xFFFF6E6E),
    outline = Color(0xFF3C3C3C)
)


@Composable
fun RegistroTicketTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = SleekDarkColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
