package com.eliaskrr.fitmacros.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Principal (inspirada en el logo)
val BrandBlue = Color(0xFF1D3557)
val AccentOrange = Color(0xFFF4A261)

// --- Tema Claro ---
val md_theme_light_primary = BrandBlue
val md_theme_light_onPrimary = Color.White
val md_theme_light_secondary = AccentOrange
val md_theme_light_background = Color(0xFFF8F9FA) // Un gris muy claro para el fondo
val md_theme_light_surface = Color.White // Blanco puro para las tarjetas
val md_theme_light_onBackground = Color(0xFF1A1C1E) // Texto principal (casi negro)
val md_theme_light_onSurface = Color(0xFF1A1C1E) // Texto en tarjetas

// --- Tema Oscuro ---
val md_theme_dark_primary = Color(0xFF82B1FF) // Un azul más claro para el modo oscuro
val md_theme_dark_onPrimary = Color(0xFF00275B)
val md_theme_dark_secondary = AccentOrange
val md_theme_dark_background = Color(0xFF121212) // Fondo oscuro estándar de Material
val md_theme_dark_surface = Color(0xFF1E1E1E) // Superficie ligeramente más clara
val md_theme_dark_onBackground = Color(0xFFE4E2E6)
val md_theme_dark_onSurface = Color(0xFFE4E2E6)


// Colores para los Macronutrientes (acordes al nuevo tema)
object NutrientColors {
    val Carbs = Color(0xFF2A9D8F)   // Verde azulado
    val Fat = AccentOrange          // Naranja/Arena
    val Protein = Color(0xFFE76F51) // Rojo coral
}
