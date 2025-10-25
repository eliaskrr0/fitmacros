package com.eliaskrr.fitmacros.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val route: String) {
    data object Home : AppScreen("home")
    data object Alimentos : AppScreen("alimentos")
    data object Opciones : AppScreen("opciones")
    data object AddEditAlimento : AppScreen("add_edit_alimento?alimentoId={alimentoId}") {
        fun createRoute(alimentoId: Int?) = "add_edit_alimento?alimentoId=$alimentoId"
    }
}

val screens = listOf(
    AppScreen.Home,
    AppScreen.Alimentos,
    AppScreen.Opciones
)


sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : NavItem(AppScreen.Home.route, "Home", Icons.Default.Home)
    data object Alimentos : NavItem(AppScreen.Alimentos.route, "Alimentos", Icons.Outlined.Fastfood)
    data object Opciones : NavItem(AppScreen.Opciones.route, "Opciones", Icons.Default.Settings)
}
