package com.eliaskrr.fitmacros.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Principal (inspirada en el logo)
val Brand = Color(0xFF43A047)

// --- Tema Claro ---
val md_theme_light_primary = Color(0xFFFDFDFD) // Borde superior e inferior
val md_theme_light_onPrimary = Color(0xFF000000) // Texto barra superior
val md_theme_light_secondary = Color(0xFFFDFDFD)
val md_theme_light_background = Color(0xFFFDFDFD) // Borde internp
val md_theme_light_surface = Color(0xFF000000) // Blanco puro para las tarjetas
val md_theme_light_onBackground = Color(0xFF000000)  // Texto principal (casi negro)
val md_theme_light_onSurface = Color(0xFF000000) // Texto barra inferior

// --- Tema Oscuro ---
val md_theme_dark_primary = Brand // Un azul más claro para el modo oscuro
val md_theme_dark_onPrimary = Brand
val md_theme_dark_secondary = Brand // La línea que fastball
val md_theme_dark_background = Brand // Fondo oscuro estándar de Material
val md_theme_dark_surface = Brand // Superficie ligeramente más clara
val md_theme_dark_onBackground = Brand
val md_theme_dark_onSurface = Brand


// Colores para los Macronutrientes (acordes al nuevo tema)
object NutrientColors {
    val Carbs = Color(0xFF2A9D8F)
    val Fat = Color(0xFFC1A726)
    val Protein = Color(0xFFE76F51)
}
