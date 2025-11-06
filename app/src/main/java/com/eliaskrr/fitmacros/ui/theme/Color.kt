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
val TextFieldContainerColor = Color(0xFF1F1F1F) // Fondo ligeramente más claro para campos de texto.


// --- Tema Claro ---
val md_theme_light_primary = PrimaryColor // Borde superior e inferior
val md_theme_light_background = PrimaryColor // Borde internp
val md_theme_light_secondary = Color(0xFFFDFDFD)
val md_theme_light_surface = Color(0xFF000000) // Blanco puro para las tarjetas
val md_theme_light_onPrimary = TextGeneralColor // Texto barra superior
val md_theme_light_onBackground = TextGeneralColor  // Texto principal (casi negro)
val md_theme_light_onSurface = TextGeneralColor // Texto barra inferior

// Colores para los Macronutrientes (acordes al nuevo tema)
object NutrientColors {
    val Carbs = Color(0xFF084942)
    val Fat = Color(0xFFC1A726)
    val Protein = Color(0xFFAD0128)
}
