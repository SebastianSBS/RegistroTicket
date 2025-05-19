package edu.ucne.registroticket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekDarkColors = darkColorScheme(
    primary = Color(0xFF82B1FF),         // Azul hielo (más frío y moderno)
    onPrimary = Color(0xFF0D0D0D),       // Casi negro para alto contraste
    background = Color(0xFF121212),      // Negro carbón
    surface = Color(0xFF1E1E1E),         // Superficies con un gris oscuro más suave
    surfaceVariant = Color(0xFF2C2C2E),  // Gris pizarra oscuro para tarjetas
    onSurface = Color(0xFFF0F0F0),       // Blanco sutil para texto
    onSurfaceVariant = Color(0xFFCCCCCC),// Texto en tarjetas o fondos intermedios
    error = Color(0xFFFF6E6E),           // Rojo suave pero visible
    outline = Color(0xFF3C3C3C)          // Líneas de borde sutiles
)


@Composable
fun RegistroTicketTheme(
    useDarkTheme: Boolean = true, // Forzamos el modo oscuro para notarse más
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = SleekDarkColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
