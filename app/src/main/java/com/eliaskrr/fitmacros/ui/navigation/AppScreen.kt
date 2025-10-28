package com.eliaskrr.fitmacros.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val route: String) {
    data object Profile : AppScreen("profile")
    data object Alimentos : AppScreen("alimentos")
    data object Dietas : AppScreen("dietas")
    data object Opciones : AppScreen("opciones")
    data object AddEditAlimento : AppScreen("add_edit_alimento?alimentoId={alimentoId}") {
        fun createRoute(alimentoId: Int?) = "add_edit_alimento?alimentoId=$alimentoId"
    }
}

sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Profile : NavItem(AppScreen.Profile.route, "Perfil", Icons.Default.Person)
    data object Alimentos : NavItem(AppScreen.Alimentos.route, "Alimentos", Icons.Outlined.Fastfood)
    data object Dietas : NavItem(AppScreen.Dietas.route, "Dietas", Icons.Default.Book)
    data object Opciones : NavItem(AppScreen.Opciones.route, "Opciones", Icons.Default.Settings)
}
