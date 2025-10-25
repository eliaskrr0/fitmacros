package com.eliaskrr.fitmacros.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : AppScreen("home", "Home", Icons.Default.Home)
    data object Alimentos : AppScreen("alimentos", "Alimentos", Icons.Outlined.Fastfood)
    data object Opciones : AppScreen("opciones", "Opciones", Icons.Default.Settings)
}
