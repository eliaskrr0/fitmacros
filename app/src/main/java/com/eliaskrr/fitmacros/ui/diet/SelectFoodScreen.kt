package com.eliaskrr.fitmacros.ui.diet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Food
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.TextCardColor
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SelectAlimentoScreen(
    viewModel: SelectFoodViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val alimentos by viewModel.alimentos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()

    var selectedFood by remember { mutableStateOf<Food?>(null) }
    var cantidad by remember { mutableStateOf("") }

    LaunchedEffect(isSaved) {
        if (isSaved) {
            onNavigateUp()
        }
    }

    if (selectedFood != null) {
        AlertDialog(
            onDismissRequest = { selectedFood = null },
            title = { Text("Añadir ${selectedFood?.nombre}") },
            text = {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad (gramos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    val amount = cantidad.toDoubleOrNull()
                    if (amount != null) {
                        viewModel.addAlimentoToDieta(selectedFood!!.id, amount)
                    }
                }) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFood = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query -> viewModel.onSearchQueryChange(query) },
                label = { Text(stringResource(R.string.search_alimento)) },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(alimentos) { alimento ->
                    AlimentoSelectItem(food = alimento, onClick = { selectedFood = alimento })
                }
            }
        }
    }
}

@Composable
fun AlimentoSelectItem(food: Food, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard,
            contentColor = TextCardColor
        )
    ) {
        Text(text = food.nombre, modifier = Modifier.padding(16.dp))
    }
}
