package com.eliaskrr.fitmacros.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Principal (inspirada en el logo)
val Brand = Color(0xFF43A047)

// --- Tema Claro ---
val md_theme_light_primary = Brand
val md_theme_light_onPrimary = Brand
val md_theme_light_secondary = Brand
val md_theme_light_background = Brand // Fondo general
val md_theme_light_surface = Brand // Blanco puro para las tarjetas
val md_theme_light_onBackground = Brand // Texto principal (casi negro)
val md_theme_light_onSurface = Brand // Texto en tarjetas

// --- Tema Oscuro ---
val md_theme_dark_primary = Color(0xFF82B1FF) // Un azul más claro para el modo oscuro
val md_theme_dark_onPrimary = Color(0xFF00275B)
val md_theme_dark_secondary = Brand // La línea que faltaba
val md_theme_dark_background = Color(0xFF121212) // Fondo oscuro estándar de Material
val md_theme_dark_surface = Color(0xFF1E1E1E) // Superficie ligeramente más clara
val md_theme_dark_onBackground = Color(0xFFE4E2E6)
val md_theme_dark_onSurface = Color(0xFFE4E2E6)


// Colores para los Macronutrientes (acordes al nuevo tema)
object NutrientColors {
    val Carbs = Color(0xFF2A9D8F)
    val Fat = Color(0xFFC1A726)
    val Protein = Color(0xFFE76F51)
}
