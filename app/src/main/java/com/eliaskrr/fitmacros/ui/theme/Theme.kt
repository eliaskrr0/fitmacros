package com.eliaskrr.fitmacros.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el Tema Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,          // Color principal para elementos interactivos como botones.
    onPrimary = md_theme_dark_onPrimary,        // Color del texto/iconos que van sobre el color primario.
    secondary = md_theme_dark_secondary,      // Color de acento para elementos menos prominentes.
    background = md_theme_dark_background,     // Color del fondo general de la aplicación.
    surface = md_theme_dark_surface,          // Color de la superficie de componentes como Cards, Menús, etc.
    onBackground = md_theme_dark_onBackground,  // Color del texto sobre el fondo general.
    onSurface = md_theme_dark_onSurface,        // Color del texto/iconos sobre las superficies (ej. texto en una Card).
    surfaceVariant = md_theme_dark_surface,     // Un matiz de la superficie, usado a menudo para fondos de TopAppBar o NavigationBar.
    onSurfaceVariant = md_theme_dark_onSurface  // Color del texto/iconos sobre surfaceVariant.
)

// Paleta de colores para el Tema Claro
private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,          // Color principal (botones, FABs, indicadores activos).
    onPrimary = md_theme_light_onPrimary,        // Texto/iconos sobre el color primario (normalmente blanco o negro).
    secondary = md_theme_light_secondary,      // Color de acento secundario.
    background = md_theme_light_background,     // Fondo de las pantallas.
    surface = md_theme_light_surface,          // Fondo de las tarjetas (Cards).
    onBackground = md_theme_light_onBackground,  // Color principal del texto de la app.
    onSurface = md_theme_light_onSurface,        // Color del texto que va dentro de las tarjetas.
    surfaceVariant = md_theme_light_background, // Color para superficies sutilmente distintas, como la barra de navegación inferior.
    onSurfaceVariant = md_theme_light_onBackground // Texto/iconos en esas superficies variantes.
)

@Composable
fun FitMacrosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
