package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Dieta
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.TextCard

@Composable
fun DietasScreen(viewModel: DietaViewModel) {
    val dietas by viewModel.allDietas.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newDietaName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.new_dieta_title)) },
            text = {
                OutlinedTextField(
                    value = newDietaName,
                    onValueChange = { newDietaName = it },
                    label = { Text(stringResource(R.string.dieta_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDietaName.isNotBlank()) {
                            viewModel.insert(Dieta(nombre = newDietaName))
                            newDietaName = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_dieta_title))
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            items(dietas) { dieta ->
                DietaItem(dieta = dieta, onClick = { /* TODO: Navegar a la pantalla de detalle */ })
            }
        }
    }
}

@Composable
fun DietaItem(dieta: Dieta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard,
            contentColor = TextCard
        )
    ) {
        Text(
            text = dieta.nombre,
            modifier = Modifier.padding(16.dp)
        )
    }
}
