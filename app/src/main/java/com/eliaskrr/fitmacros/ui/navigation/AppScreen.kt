package com.eliaskrr.fitmacros.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Flatware
import androidx.compose.material.icons.sharp.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val route: String) {
    data object Profile : AppScreen("profile")
    data object Alimentos : AppScreen("alimentos")
    data object Dietas : AppScreen("dietas")
    data object Opciones : AppScreen("opciones")
    data object About : AppScreen("about")
    data object Export : AppScreen("export")
    data object Notifications : AppScreen("notifications")
    data object AddEditAlimento : AppScreen("add_edit_alimento?alimentoId={alimentoId}") {
        fun createRoute(alimentoId: Int?) = "add_edit_alimento?alimentoId=$alimentoId"
    }
    data object DietaDetail : AppScreen("dieta_detail/{dietaId}") {
        fun createRoute(dietaId: Int) = "dieta_detail/$dietaId"
    }
    data object SelectAlimentoForMeal : AppScreen("select_alimento/{dietaId}/{mealType}") {
        fun createRoute(dietaId: Int, mealType: com.eliaskrr.fitmacros.data.model.MealType) =
            "select_alimento/$dietaId/${mealType.name}"
    }
}

sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Profile : NavItem(AppScreen.Profile.route, "Perfil", Icons.Sharp.Person)
    data object Alimentos : NavItem(AppScreen.Alimentos.route, "Alimentos", Icons.Outlined.Flatware)
    data object Dietas : NavItem(AppScreen.Dietas.route, "Dietas", Icons.Default.Book)
    data object Opciones : NavItem(AppScreen.Opciones.route, "Opciones", Icons.Default.Settings)
}
