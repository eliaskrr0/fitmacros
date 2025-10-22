package com.eliaskrr.fitmacros.ui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.FitMacrosApplication
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModel
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModelFactory

class MainActivity : ComponentActivity() {

    private val alimentoViewModel: AlimentoViewModel by viewModels {
        AlimentoViewModelFactory((application as FitMacrosApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AlimentosScreen(viewModel = alimentoViewModel)
                }
            }
        }
    }
}

@Composable
fun AlimentosScreen(viewModel: AlimentoViewModel) {
    val alimentos by viewModel.allAlimentos.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Creamos un alimento de prueba con valores aleatorios
                val nuevoAlimento = Alimento(
                    nombre = "Alimento de prueba ${System.currentTimeMillis() % 100}",
                    proteinas = 20.5,
                    carbos = 10.2,
                    grasas = 5.8,
                    calorias = 150.0
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
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(text = alimento.nombre)
    }
}
