package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.eliaskrr.fitmacros.ui.theme.ButtonCancelColor
import com.eliaskrr.fitmacros.ui.theme.ButtonConfirmColor
import com.eliaskrr.fitmacros.ui.theme.DialogBackgroundColor
import com.eliaskrr.fitmacros.ui.theme.DialogTextColor
import com.eliaskrr.fitmacros.ui.theme.DialogTitleColor
import com.eliaskrr.fitmacros.ui.theme.ColorTextCard

@Composable
fun DietasScreen(viewModel: DietaViewModel, onDietaClick: (Int) -> Unit) {
    val dietas by viewModel.allDietas.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newDietaName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = DialogBackgroundColor,
            titleContentColor = DialogTitleColor,
            textContentColor = DialogTextColor,
            title = { Text(stringResource(R.string.new_dieta_title)) },
            text = {
                OutlinedTextField(
                    value = newDietaName,
                    onValueChange = { newDietaName = it },
                    label = { Text(stringResource(R.string.dieta_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundCard,
                        unfocusedContainerColor = BackgroundCard,
                        disabledContainerColor = BackgroundCard,
                        cursorColor = ColorTextCard,
                        focusedBorderColor = ColorTextCard.copy(alpha = 0.8f),
                        unfocusedBorderColor = ColorTextCard.copy(alpha = 0.5f),
                        focusedLabelColor = ColorTextCard.copy(alpha = 0.8f),
                        unfocusedLabelColor = ColorTextCard.copy(alpha = 0.5f),
                        focusedTextColor = ColorTextCard,
                        unfocusedTextColor = ColorTextCard
                    )
                )
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonCancelColor)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                 Button(
                    onClick = {
                        if (newDietaName.isNotBlank()) {
                            viewModel.insert(Dieta(nombre = newDietaName))
                            newDietaName = ""
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonConfirmColor)
                ) {
                    Text(stringResource(R.string.create))
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
                DietaItem(dieta = dieta, onClick = { onDietaClick(dieta.id) })
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
            contentColor = ColorTextCard
        )
    ) {
        Text(
            text = dieta.nombre,
            modifier = Modifier.padding(16.dp),
            color = ColorTextCard
        )
    }
}
