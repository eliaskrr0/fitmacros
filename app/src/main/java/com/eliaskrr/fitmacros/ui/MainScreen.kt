package com.eliaskrr.fitmacros.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Flatware
import androidx.compose.material.icons.sharp.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType

sealed class AppScreen(val route: String) {
    data object Profile : AppScreen("profile")
    data object Food : AppScreen("alimentos")
    data object Diet : AppScreen("dietas")
    data object Setting : AppScreen("opciones")
    data object About : AppScreen("about")
    data object Export : AppScreen("export")
    data object Notifications : AppScreen("notifications")
    data object AddEditFood : AppScreen("add_edit_food?foodId={foodId}") {
        fun createRoute(foodId: Int?) = "add_edit_food?foodId=$foodId"
    }
    data object DietaDetail : AppScreen("dieta_detail/{dietId}") {
        fun createRoute(dietId: Int) = "dieta_detail/$dietId"
    }
    data object SelectAlimentoForMeal : AppScreen("select_food/{dietId}/{mealType}") {
        fun createRoute(dietId: Int, mealType: MealType) =
            "select_food/$dietId/${mealType.name}"
    }
}

sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Profile : NavItem(AppScreen.Profile.route, "Perfil", Icons.Sharp.Person)
    data object Food : NavItem(AppScreen.Food.route, "Alimentos", Icons.Outlined.Flatware)
    data object Diet : NavItem(AppScreen.Diet.route, "Dietas", Icons.Default.Book)
    data object Setting : NavItem(AppScreen.Setting.route, "Opciones", Icons.Default.Settings)
}
