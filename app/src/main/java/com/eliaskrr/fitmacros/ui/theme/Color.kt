package com.eliaskrr.fitmacros.ui.theme

import androidx.compose.ui.graphics.Color

// Tema principal
val Brand = Color(0xFF000000)
val TextGeneral = Color(0xFFFFFFFF)

// Card
val BackgroundCard = Color(0xFF1C1B1B)
val TextCard = Color(0xFFF6F6F6)

// Diálogo
val DialogBackgroundColor = Color(0xFF1E1E1E) // Color de fondo para diálogos emergentes en modo oscuro.
val DialogTitleColor = TextCard // Color para el título de los diálogos.
val DialogTextColor = TextCard.copy(alpha = 0.8f) // Color para el texto del contenido de los diálogos.


// --- Tema Claro ---
val md_theme_light_primary = Brand // Borde superior e inferior
val md_theme_light_background = Brand // Borde internp
val md_theme_light_secondary = Color(0xFFFDFDFD)
val md_theme_light_surface = Color(0xFF000000) // Blanco puro para las tarjetas
val md_theme_light_onPrimary = TextGeneral // Texto barra superior
val md_theme_light_onBackground = TextGeneral  // Texto principal (casi negro)
val md_theme_light_onSurface = TextGeneral // Texto barra inferior

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
    val Carbs = Color(0xFF084942)
    val Fat = Color(0xFFC1A726)
    val Protein = Color(0xFFAD0128)
}
