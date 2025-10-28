package com.eliaskrr.fitmacros.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eliaskrr.fitmacros.FitMacrosApplication
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.ui.alimento.AddEditAlimentoScreen
import com.eliaskrr.fitmacros.ui.alimento.AddEditAlimentoViewModel
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModel
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModelFactory
import com.eliaskrr.fitmacros.ui.dieta.DietaDetailScreen
import com.eliaskrr.fitmacros.ui.dieta.DietaDetailViewModel
import com.eliaskrr.fitmacros.ui.dieta.DietasScreen
import com.eliaskrr.fitmacros.ui.dieta.DietaViewModel
import com.eliaskrr.fitmacros.ui.dieta.DietaViewModelFactory
import com.eliaskrr.fitmacros.ui.navigation.AppScreen
import com.eliaskrr.fitmacros.ui.navigation.NavItem
import com.eliaskrr.fitmacros.ui.opciones.OptionsScreen
import com.eliaskrr.fitmacros.ui.profile.PersonalDataScreen
import com.eliaskrr.fitmacros.ui.profile.ProfileScreen
import com.eliaskrr.fitmacros.ui.profile.ProfileViewModel
import com.eliaskrr.fitmacros.ui.profile.ProfileViewModelFactory
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.FitMacrosTheme
import com.eliaskrr.fitmacros.ui.theme.TextCard

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            FitMacrosTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val application = application as FitMacrosApplication
                    val alimentoViewModel: AlimentoViewModel by viewModels {
                        AlimentoViewModelFactory(application.alimentoRepository)
                    }
                    val profileViewModel: ProfileViewModel by viewModels {
                        ProfileViewModelFactory(application.userDataRepository)
                    }
                    val dietaViewModel: DietaViewModel by viewModels {
                        DietaViewModelFactory(application.dietaRepository)
                    }
                    MainScreen(alimentoViewModel = alimentoViewModel, profileViewModel = profileViewModel, dietaViewModel = dietaViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(alimentoViewModel: AlimentoViewModel, profileViewModel: ProfileViewModel, dietaViewModel: DietaViewModel) {
    val navController = rememberNavController()
    val navItems = listOf(NavItem.Profile, NavItem.Alimentos, NavItem.Dietas, NavItem.Opciones)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FitMacros", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
                navItems.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.label) },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navigateToScreen(navController, screen.route) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == AppScreen.Alimentos.route) {
                FloatingActionButton(onClick = { navController.navigate(AppScreen.AddEditAlimento.createRoute(null)) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Alimento")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Profile.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreen.Profile.route) { 
                ProfileScreen(
                    viewModel = profileViewModel,
                    onEditClick = { navController.navigate("personal_data") }
                ) 
            }
            composable(AppScreen.Alimentos.route) {
                AlimentosScreen(
                    viewModel = alimentoViewModel,
                    onAlimentoClick = { navController.navigate(AppScreen.AddEditAlimento.createRoute(it.id)) }
                )
            }
            composable(AppScreen.Dietas.route) { 
                DietasScreen(
                    viewModel = dietaViewModel,
                    onDietaClick = { dietaId ->
                        navController.navigate(AppScreen.DietaDetail.createRoute(dietaId))
                    }
                ) 
            }
            composable(AppScreen.Opciones.route) { OptionsScreen() }
            composable(
                route = AppScreen.AddEditAlimento.route,
                arguments = listOf(navArgument("alimentoId") { type = NavType.IntType; defaultValue = -1 })
            ) {
                val application = navController.context.applicationContext as FitMacrosApplication
                val addEditViewModel: AddEditAlimentoViewModel = viewModel(
                    factory = AddEditAlimentoViewModel.provideFactory(application.alimentoRepository)
                )
                AddEditAlimentoScreen(
                    viewModel = addEditViewModel,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable("personal_data") {
                val userData by profileViewModel.userData.collectAsState()
                PersonalDataScreen(
                    userData = userData,
                    onSave = { 
                        profileViewModel.saveUserData(it)
                        navController.navigateUp()
                    },
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable(
                route = AppScreen.DietaDetail.route,
                arguments = listOf(navArgument("dietaId") { type = NavType.IntType })
            ) {
                val application = navController.context.applicationContext as FitMacrosApplication
                val detailViewModel: DietaDetailViewModel = viewModel(
                    factory = DietaDetailViewModel.provideFactory(application.userDataRepository)
                )
                DietaDetailScreen(viewModel = detailViewModel)
            }
        }
    }
}

fun navigateToScreen(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AlimentosScreen(
    viewModel: AlimentoViewModel,
    onAlimentoClick: (Alimento) -> Unit
) {
    val alimentos by viewModel.alimentos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Buscar alimento...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundCard,
                unfocusedContainerColor = BackgroundCard,
                disabledContainerColor = BackgroundCard,
                cursorColor = TextCard,
                focusedBorderColor = TextCard.copy(alpha = 0.8f),
                unfocusedBorderColor = TextCard.copy(alpha = 0.5f),
                focusedLabelColor = TextCard.copy(alpha = 0.8f),
                unfocusedLabelColor = TextCard.copy(alpha = 0.5f),
                focusedLeadingIconColor = TextCard.copy(alpha = 0.8f),
                unfocusedLeadingIconColor = TextCard.copy(alpha = 0.5f)
            )
        )
        LazyColumn {
            items(alimentos) { alimento ->
                AlimentoItem(alimento = alimento, onClick = { onAlimentoClick(alimento) })
            }
        }
    }
}

@Composable
fun AlimentoItem(alimento: Alimento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard,
            contentColor = TextCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alimento.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                alimento.marca?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextCard.copy(alpha = 0.8f)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.1f", alimento.calorias)} kcal",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "P: ${String.format("%.1f", alimento.proteinas)}g", fontSize = 12.sp)
                    Text(text = "C: ${String.format("%.1f", alimento.carbos)}g", fontSize = 12.sp)
                    Text(text = "G: ${String.format("%.1f", alimento.grasas)}g", fontSize = 12.sp)
                }
            }
        }
    }
}
