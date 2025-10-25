package com.eliaskrr.fitmacros.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eliaskrr.fitmacros.FitMacrosApplication
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModel
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModelFactory
import com.eliaskrr.fitmacros.ui.home.HomeScreen
import com.eliaskrr.fitmacros.ui.navigation.AppScreen
import com.eliaskrr.fitmacros.ui.opciones.OptionsScreen

class MainActivity : ComponentActivity() {

    private val alimentoViewModel: AlimentoViewModel by viewModels {
        AlimentoViewModelFactory((application as FitMacrosApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(viewModel = alimentoViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: AlimentoViewModel) {
    val navController = rememberNavController()
    val screens = listOf(AppScreen.Home, AppScreen.Alimentos, AppScreen.Opciones)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.label) },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreen.Home.route) { HomeScreen() }
            composable(AppScreen.Alimentos.route) { AlimentosScreen(viewModel) }
            composable(AppScreen.Opciones.route) { OptionsScreen() }
        }
    }
}

@Composable
fun AlimentosScreen(viewModel: AlimentoViewModel) {
    val alimentos by viewModel.allAlimentos.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val nuevoAlimento = Alimento(
                    nombre = "Pechuga de Pollo",
                    marca = "Marca Ejemplo",
                    proteinas = 25.0,
                    carbos = 0.0,
                    grasas = 3.5,
                    calorias = 165.0
                )
                viewModel.insert(nuevoAlimento)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Alimento")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(alimentos) { alimento ->
                AlimentoItem(alimento = alimento)
            }
        }
    }
}

@Composable
fun AlimentoItem(alimento: Alimento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
