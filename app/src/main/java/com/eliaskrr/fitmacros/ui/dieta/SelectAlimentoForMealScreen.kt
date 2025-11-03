package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.data.model.QuantityUnit
import com.eliaskrr.fitmacros.ui.alimento.AlimentoViewModel
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.TextCardColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAlimentoForMealScreen(
    alimentoViewModel: AlimentoViewModel,
    mealType: MealType,
    onAlimentoSelected: (Alimento, Double, QuantityUnit) -> Unit,
    onNavigateUp: () -> Unit
) {
    val alimentos by alimentoViewModel.alimentos.collectAsState()
    val searchQuery by alimentoViewModel.searchQuery.collectAsState()

    var selectedAlimento by remember { mutableStateOf<Alimento?>(null) }
    var quantityText by remember { mutableStateOf("100") }
    var selectedUnit by remember { mutableStateOf(QuantityUnit.GRAMS) }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        alimentoViewModel.onSearchQueryChange("")
    }

    if (selectedAlimento != null) {
        QuantityDialog(
            alimento = selectedAlimento!!,
            mealType = mealType,
            quantityText = quantityText,
            onQuantityChange = {
                quantityText = it
                showError = false
            },
            selectedUnit = selectedUnit,
            onUnitSelected = { selectedUnit = it },
            showError = showError,
            onDismiss = {
                selectedAlimento = null
                quantityText = "100"
                selectedUnit = QuantityUnit.GRAMS
                showError = false
            },
            onConfirm = {
                val normalized = quantityText.replace(',', '.').toDoubleOrNull()
                if (normalized == null || normalized <= 0.0) {
                    showError = true
                    return@QuantityDialog
                }
                onAlimentoSelected(selectedAlimento!!, normalized, selectedUnit)
                selectedAlimento = null
                quantityText = "100"
                selectedUnit = QuantityUnit.GRAMS
                showError = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.add_to_meal, stringResource(mealType.stringRes)))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.select_alimento_instruction, stringResource(mealType.stringRes)),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = alimentoViewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(stringResource(R.string.search_food_placeholder)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
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
            LazyColumn {
                items(alimentos) { alimento ->
                    com.eliaskrr.fitmacros.ui.AlimentoItem(
                        alimento = alimento,
                        onClick = { selectedAlimento = alimento }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuantityDialog(
    alimento: Alimento,
    mealType: MealType,
    quantityText: String,
    onQuantityChange: (String) -> Unit,
    selectedUnit: QuantityUnit,
    onUnitSelected: (QuantityUnit) -> Unit,
    showError: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.select_quantity_for, alimento.nombre))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = onQuantityChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.quantity)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    isError = showError,
                    supportingText = if (showError) {
                        { Text(stringResource(R.string.invalid_quantity)) }
                    } else null
                )
                Spacer(modifier = Modifier.height(12.dp))
                QuantityUnitDropdown(
                    selectedUnit = selectedUnit,
                    onUnitSelected = onUnitSelected
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.add_to_meal, stringResource(mealType.stringRes)))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuantityUnitDropdown(
    selectedUnit: QuantityUnit,
    onUnitSelected: (QuantityUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = stringResource(selectedUnit.labelRes),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.unit_label)) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundCard,
                unfocusedContainerColor = BackgroundCard,
                disabledContainerColor = BackgroundCard,
                cursorColor = TextCardColor,
                focusedBorderColor = TextCardColor.copy(alpha = 0.8f),
                unfocusedBorderColor = TextCardColor.copy(alpha = 0.5f),
                focusedLabelColor = TextCardColor.copy(alpha = 0.8f),
                unfocusedLabelColor = TextCardColor.copy(alpha = 0.5f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            QuantityUnit.values().forEach { unit ->
                DropdownMenuItem(
                    text = { Text(stringResource(unit.labelRes)) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
