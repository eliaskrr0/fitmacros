package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.ui.AlimentoItem
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.TextCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlimentoToDietaScreen(
    viewModel: AddAlimentoToDietaViewModel,
    onNavigateUp: () -> Unit
) {
    val alimentos by viewModel.alimentos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val mealName = stringResource(id = viewModel.mealType.stringRes)
    val searchPlaceholder = stringResource(R.string.search_alimento_placeholder)

    var selectedAlimento by remember { mutableStateOf<Alimento?>(null) }
    var cantidadText by remember { mutableStateOf("100") }
    var showDialog by remember { mutableStateOf(false) }
    var cantidadError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                AddAlimentoToDietaViewModel.UiEvent.AlimentoAdded -> onNavigateUp()
            }
        }
    }

    if (showDialog && selectedAlimento != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(
                        R.string.add_alimento_to_meal_dialog_title,
                        selectedAlimento!!.nombre
                    )
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = {
                            cantidadText = it
                            if (cantidadError) cantidadError = false
                        },
                        label = { Text(stringResource(R.string.quantity_in_grams)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (cantidadError) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.invalid_quantity_message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cantidad = cantidadText
                            .replace(',', '.')
                            .toDoubleOrNull()
                        if (cantidad != null && cantidad > 0) {
                            viewModel.addAlimentoToDieta(selectedAlimento!!.id, cantidad)
                            showDialog = false
                        } else {
                            cantidadError = true
                        }
                    }
                ) {
                    Text(stringResource(R.string.add_alimento))
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
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.add_alimento_to_meal_title, mealName))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text(searchPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = searchPlaceholder
                    )
                },
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

            if (alimentos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_alimentos_found),
                        color = TextCard.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(alimentos) { alimento ->
                        AlimentoItem(
                            alimento = alimento,
                            onClick = {
                                selectedAlimento = alimento
                                cantidadText = "100"
                                showDialog = true
                                cantidadError = false
                            }
                        )
                    }
                }
            }
        }
    }
}
