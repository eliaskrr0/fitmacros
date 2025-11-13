package com.eliaskrr.fitmacros.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DockedSearchBar
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
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
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import com.eliaskrr.fitmacros.data.entity.nutrition.Diet
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import com.eliaskrr.fitmacros.ui.food.AddEditAlimentoScreen
import com.eliaskrr.fitmacros.ui.food.AddEditAlimentoViewModel
import com.eliaskrr.fitmacros.ui.food.FoodViewModel
import com.eliaskrr.fitmacros.ui.diet.DietaDetailScreen
import com.eliaskrr.fitmacros.ui.diet.DietaDetailViewModel
import com.eliaskrr.fitmacros.ui.diet.DietasScreen
import com.eliaskrr.fitmacros.ui.diet.DietViewModel
import com.eliaskrr.fitmacros.ui.diet.SelectAlimentoForMealScreen
import com.eliaskrr.fitmacros.ui.setting.AboutScreen
import com.eliaskrr.fitmacros.ui.setting.ExportScreen
import com.eliaskrr.fitmacros.ui.setting.NotificationsScreen
import com.eliaskrr.fitmacros.ui.setting.OptionsScreen
import com.eliaskrr.fitmacros.ui.profile.PersonalDataScreen
import com.eliaskrr.fitmacros.ui.profile.ProfileScreen
import com.eliaskrr.fitmacros.ui.profile.ProfileViewModel
import com.eliaskrr.fitmacros.ui.components.SelectionActionBar
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.FitMacrosTheme
import com.eliaskrr.fitmacros.ui.theme.TextCardColor
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val foodViewModel: FoodViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val dietViewModel: DietViewModel by viewModels()

    private var fileSaverCallback: ((OutputStream) -> Unit)? = null

    private val fileSaverLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { contentResolver.openOutputStream(it)?.use { stream -> fileSaverCallback?.invoke(stream) } }
    }

    private fun exportDietaToCsv(diet: Diet) {
        lifecycleScope.launch {
            val alimentosDeLaDieta = dietViewModel.getAlimentosOfDieta(diet.id)

            fileSaverCallback = { outputStream ->
                val writer = outputStream.bufferedWriter()
                writer.write("Alimento,Marca,Cantidad,Unidad,Calorías,Proteínas,Carbohidratos,Grasas\n")
                alimentosDeLaDieta.forEach { dietaAlimento ->
                    val alimento = dietaAlimento.food
                    writer.write(
                        "${alimento.name}," +
                                "${alimento.brand ?: ""}," +
                                "${dietaAlimento.amount}," +
                                "${dietaAlimento.unit}," +
                                "${alimento.calories}," +
                                "${alimento.proteins}," +
                                "${alimento.carbs}," +
                                "${alimento.fats}\n"
                    )
                }
                writer.flush()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Dieta exportada con éxito", Toast.LENGTH_SHORT).show()
                }
            }
            fileSaverLauncher.launch("${diet.name}.csv")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            FitMacrosTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(
                        foodViewModel = foodViewModel,
                        profileViewModel = profileViewModel,
                        dietViewModel = dietViewModel,
                        onExportDieta = ::exportDietaToCsv
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    foodViewModel: FoodViewModel,
    profileViewModel: ProfileViewModel,
    dietViewModel: DietViewModel,
    onExportDieta: (Diet) -> Unit
) {
    val navController = rememberNavController()
    val navItems = listOf(NavItem.Profile, NavItem.Food, NavItem.Diet, NavItem.Setting)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }

    val showBottomBar = navItems.any { it.route == currentDestination?.route }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (showBottomBar) {
                CenterAlignedTopAppBar(
                    title = { Text("FitMacros", color = MaterialTheme.colorScheme.onPrimary) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
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
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == AppScreen.Food.route) {
                FloatingActionButton(onClick = { navController.navigate(AppScreen.AddEditFood.createRoute(null)) }) {
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
            composable(AppScreen.Food.route) {
                AlimentosScreen(
                    viewModel = foodViewModel,
                    onAlimentoClick = { navController.navigate(AppScreen.AddEditFood.createRoute(it.id)) },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(AppScreen.Diet.route) {
                DietasScreen(
                    viewModel = dietViewModel,
                    onDietaClick = { dietId ->
                        navController.navigate(AppScreen.DietaDetail.createRoute(dietId))
                    }
                ) 
            }
            composable(AppScreen.Setting.route) { OptionsScreen(navController = navController) }
            composable(AppScreen.About.route) { AboutScreen(onNavigateUp = { navController.navigateUp() }) }
            composable(AppScreen.Export.route) {
                ExportScreen(
                    dietViewModel = dietViewModel,
                    onNavigateUp = { navController.navigateUp() },
                    onDietaSelected = onExportDieta
                )
            }
            composable(AppScreen.Notifications.route) {
                NotificationsScreen(onNavigateUp = { navController.navigateUp() })
            }
            composable(
                route = AppScreen.AddEditFood.route,
                arguments = listOf(navArgument("foodId") { type = NavType.IntType; defaultValue = -1 })
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
                arguments = listOf(navArgument("dietId") { type = NavType.IntType })
            ) { backStackEntry ->
                val dietId = backStackEntry.arguments?.getInt("dietId") ?: return@composable
                val detailViewModel: DietaDetailViewModel = hiltViewModel()
                DietaDetailScreen(
                    viewModel = detailViewModel,
                    onAddAlimentoClick = { mealType ->
                        navController.navigate(AppScreen.SelectAlimentoForMeal.createRoute(dietId, mealType))
                    },
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable(
                route = AppScreen.SelectAlimentoForMeal.route,
                arguments = listOf(
                    navArgument("dietId") { type = NavType.IntType },
                    navArgument("mealType") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val mealTypeArg = backStackEntry.arguments?.getString("mealType") ?: MealType.BREAKFAST.name
                val mealType = runCatching { MealType.valueOf(mealTypeArg) }.getOrDefault(MealType.BREAKFAST)
                val detailViewModel: DietaDetailViewModel = hiltViewModel()
                SelectAlimentoForMealScreen(
                    foodViewModel = foodViewModel,
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
    viewModel: FoodViewModel,
    onAlimentoClick: (Food) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is FoodViewModel.AlimentoEvent.ShowMessage ->
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
        var isSearchActive by rememberSaveable { mutableStateOf(false) }

        DockedSearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onSearch = { isSearchActive = false },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_food)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_food)) }
        ) {
        }
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
        LazyColumn (
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.padding(16.dp)
		) {
            if (uiState.foods.isEmpty() && !uiState.isLoading) {
                item {
                    val messageRes = if (searchQuery.isBlank()) {
                        R.string.no_foods_available
                    } else {
                        R.string.no_foods_found
                    }
                    Text(
                        text = stringResource(messageRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    )
                }
            } else {
                items(
                    items = uiState.foods,
                    key = { it.id }
                ) { alimento ->
                    val isSelected = uiState.selectedAlimentos.contains(alimento.id)
                    AlimentoItem(
                        food = alimento,
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlimentoItem(
    food: Food,
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
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                food.brand?.let {
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
                    text = "${String.format(Locale.getDefault(), "%.1f", food.calories)} kcal",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "P: ${String.format(Locale.getDefault(), "%.1f", food.proteins)}g", fontSize = 12.sp)
                    Text(text = "C: ${String.format(Locale.getDefault(),"%.1f", food.carbs)}g", fontSize = 12.sp)
                    Text(text = "G: ${String.format(Locale.getDefault(),"%.1f", food.fats)}g", fontSize = 12.sp)
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
