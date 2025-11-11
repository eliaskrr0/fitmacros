package com.eliaskrr.fitmacros.ui.theme

import androidx.compose.ui.graphics.Color

// Colores principales
val PrimaryColor = Color(0xFF0A0A0A)
val SecondaryColor = Color(0xFF151515)
val TertiaryColor = Color(0xFF2A2A2A)
val TextPrimaryColor = Color(0xFFF2F2F2)

// Color de fondo
val BackgroundCard = SecondaryColor
val DialogBackgroundColor = SecondaryColor
val TextFieldContainerColor = SecondaryColor // Cuadro de texto

// Colores del textp
val TextCardColor = TextPrimaryColor
val DialogTitleColor = TextCardColor
val DialogTextColor = TextPrimaryColor


// Color de los botones
val ButtonConfirmColor = Color(0xFF33D17A) // Azul para botones de confirmación.
val ButtonCancelColor = Color(0xFF9A9A9A) // Rojo para botones de cancelación.


// Tema del proyecto
val md_theme_light_primary = PrimaryColor // Borde superior e inferior
val md_theme_light_secondary = SecondaryColor
val md_theme_light_tertiary = TertiaryColor
val md_theme_light_background = PrimaryColor // Borde internp
val md_theme_light_surface = Color(0xFF000000) // Blanco puro para las tarjetas
val md_theme_light_onPrimary = TextPrimaryColor // Texto barra superior
val md_theme_light_onBackground = TextPrimaryColor  // Texto principal (casi negro)
val md_theme_light_onSurface = TextPrimaryColor // Texto barra inferior

// Colores para los Macronutrientes (acordes al nuevo tema)
object NutrientColors {
    val Carbs = Color(0xFF2AA9A6)
    val Fat = Color(0xFFC6A230)
    val Protein = Color(0xFFD2546A)
    val Calories = Color(0xFF339C2F)
}
