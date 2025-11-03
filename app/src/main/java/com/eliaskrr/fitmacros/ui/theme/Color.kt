package com.eliaskrr.fitmacros.ui.theme

import androidx.compose.ui.graphics.Color

// Tema principal
val PrimaryColor = Color(0xFF000000)
val TextGeneralColor = Color(0xFFFFFFFF)

// Card
val BackgroundCard = Color(0xFF1C1B1B)
val TextCardColor = Color(0xFFF6F6F6)

// Diálogo
val DialogBackgroundColor = Color(0xFF1E1E1E) // Color de fondo para diálogos emergentes en modo oscuro.
val DialogTitleColor = TextCardColor // Color para el título de los diálogos.
val DialogTextColor = TextCardColor.copy(alpha = 0.8f) // Color para el texto del contenido de los diálogos.
val ButtonConfirmColor = Color(0xFF3A86F4) // Azul para botones de confirmación.
val ButtonCancelColor = Color(0xFFE53935) // Rojo para botones de cancelación.


// --- Tema Claro ---
val md_theme_light_primary = PrimaryColor // Borde superior e inferior
val md_theme_light_background = PrimaryColor // Borde internp
val md_theme_light_secondary = Color(0xFFFDFDFD)
val md_theme_light_surface = Color(0xFF000000) // Blanco puro para las tarjetas
val md_theme_light_onPrimary = TextGeneralColor // Texto barra superior
val md_theme_light_onBackground = TextGeneralColor  // Texto principal (casi negro)
val md_theme_light_onSurface = TextGeneralColor // Texto barra inferior

// --- Tema Oscuro ---
val md_theme_dark_primary = PrimaryColor // Un azul más claro para el modo oscuro
val md_theme_dark_onPrimary = PrimaryColor
val md_theme_dark_secondary = PrimaryColor // La línea que fastball
val md_theme_dark_background = PrimaryColor // Fondo oscuro estándar de Material
val md_theme_dark_surface = PrimaryColor // Superficie ligeramente más clara
val md_theme_dark_onBackground = PrimaryColor
val md_theme_dark_onSurface = PrimaryColor


// Colores para los Macronutrientes (acordes al nuevo tema)
object NutrientColors {
    val Carbs = Color(0xFF084942)
    val Fat = Color(0xFFC1A726)
    val Protein = Color(0xFFAD0128)
}
