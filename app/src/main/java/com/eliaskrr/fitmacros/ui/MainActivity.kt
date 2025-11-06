package com.eliaskrr.fitmacros.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.ui.alimento.AddEditAlimentoScreen
import com.eliaskrr.fitmacros.ui.alimento.AddEditAlimentoViewModel
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModel
import com.eliaskrr.fitmacros.ui.dieta.DietaDetailScreen
import com.eliaskrr.fitmacros.ui.dieta.DietaDetailViewModel
import com.eliaskrr.fitmacros.ui.dieta.DietasScreen
import com.eliaskrr.fitmacros.ui.dieta.DietaViewModel
import com.eliaskrr.fitmacros.ui.dieta.SelectAlimentoForMealScreen
import com.eliaskrr.fitmacros.ui.navigation.AppScreen
import com.eliaskrr.fitmacros.ui.navigation.NavItem
import com.eliaskrr.fitmacros.ui.opciones.OptionsScreen
import com.eliaskrr.fitmacros.ui.profile.PersonalDataScreen
import com.eliaskrr.fitmacros.ui.profile.ProfileScreen
import com.eliaskrr.fitmacros.ui.profile.ProfileViewModel
import com.eliaskrr.fitmacros.ui.components.SelectionActionBar
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.FitMacrosTheme
import com.eliaskrr.fitmacros.ui.theme.TextCardColor
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val alimentoViewModel: AlimentoViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val dietaViewModel: DietaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            FitMacrosTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    onAlimentoClick = { navController.navigate(AppScreen.AddEditAlimento.createRoute(it.id)) },
                    snackbarHostState = snackbarHostState
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
                val addEditViewModel: AddEditAlimentoViewModel = hiltViewModel()
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
            ) { backStackEntry ->
                val dietaId = backStackEntry.arguments?.getInt("dietaId") ?: return@composable
                val detailViewModel: DietaDetailViewModel = hiltViewModel()
                DietaDetailScreen(
                    viewModel = detailViewModel,
                    onAddAlimentoClick = { mealType ->
                        navController.navigate(AppScreen.SelectAlimentoForMeal.createRoute(dietaId, mealType))
                    },
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable(
                route = AppScreen.SelectAlimentoForMeal.route,
                arguments = listOf(
                    navArgument("dietaId") { type = NavType.IntType },
                    navArgument("mealType") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val mealTypeArg = backStackEntry.arguments?.getString("mealType") ?: MealType.BREAKFAST.name
                val mealType = runCatching { MealType.valueOf(mealTypeArg) }.getOrDefault(MealType.BREAKFAST)
                val detailViewModel: DietaDetailViewModel = hiltViewModel()
                SelectAlimentoForMealScreen(
                    alimentoViewModel = alimentoViewModel,
                    mealType = mealType,
                    onAlimentoSelected = { alimento, cantidad, unidad ->
                        detailViewModel.addAlimentoToDieta(alimento.id, mealType, cantidad, unidad)
                        navController.navigateUp()
                    },
                    onNavigateUp = { navController.navigateUp() }
                )
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
    onAlimentoClick: (Alimento) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AlimentoViewModel.AlimentoEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
            }
        }
    }

    Column {
        if (uiState.isSelectionMode) {
            SelectionActionBar(
                selectedCount = uiState.selectedAlimentos.size,
                onClearSelection = viewModel::clearSelection,
                onDeleteSelected = viewModel::deleteSelected
            )
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text(stringResource(R.string.search_alimento)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_alimento)) },
            singleLine = true,
            enabled = !uiState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundCard,
                unfocusedContainerColor = BackgroundCard,
                disabledContainerColor = BackgroundCard,
                cursorColor = TextCardColor,
                focusedBorderColor = TextCardColor.copy(alpha = 0.8f),
                unfocusedBorderColor = TextCardColor.copy(alpha = 0.5f),
                focusedLabelColor = TextCardColor.copy(alpha = 0.8f),
                unfocusedLabelColor = TextCardColor.copy(alpha = 0.5f),
                focusedLeadingIconColor = TextCardColor.copy(alpha = 0.8f),
                unfocusedLeadingIconColor = TextCardColor.copy(alpha = 0.5f)
            )
        )
        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
        uiState.errorMessage?.let { messageRes ->
            Text(
                text = stringResource(messageRes),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        LazyColumn {
            items(uiState.alimentos) { alimento ->
                val isSelected = uiState.selectedAlimentos.contains(alimento.id)
                AlimentoItem(
                    alimento = alimento,
                    isSelected = isSelected,
                    selectionMode = uiState.isSelectionMode,
                    onClick = {
                        if (uiState.isSelectionMode) {
                            viewModel.toggleSelection(alimento.id)
                        } else {
                            onAlimentoClick(alimento)
                        }
                    },
                    onLongClick = { viewModel.toggleSelection(alimento.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlimentoItem(
    alimento: Alimento,
    isSelected: Boolean = false,
    selectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        BackgroundCard
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        TextCardColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
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
                        color = contentColor.copy(alpha = 0.8f)
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
            if (selectionMode) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null
                )
            }
        }
    }
}

