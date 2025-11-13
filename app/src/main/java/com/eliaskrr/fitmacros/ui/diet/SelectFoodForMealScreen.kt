package com.eliaskrr.fitmacros.ui.diet

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit
import com.eliaskrr.fitmacros.ui.food.FoodViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAlimentoForMealScreen(
    foodViewModel: FoodViewModel,
    mealType: MealType,
    onAlimentoSelected: (Food, Double, QuantityUnit) -> Unit,
    onNavigateUp: () -> Unit
) {
    val alimentosUiState by foodViewModel.uiState.collectAsState()
    val searchQuery by foodViewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedFood by remember { mutableStateOf<Food?>(null) }
    var quantityText by remember { mutableStateOf("100") }
    var showError by remember { mutableStateOf(false) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        foodViewModel.onSearchQueryChange("")
    }

    LaunchedEffect(foodViewModel) {
        foodViewModel.events.collect { event ->
            when (event) {
                is FoodViewModel.AlimentoEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
            }
        }
    }

    if (selectedFood != null) {
        QuantityDialog(
            food = selectedFood!!,
            mealType = mealType,
            quantityText = quantityText,
            onQuantityChange = {
                quantityText = it
                showError = false
            },
            showError = showError,
            onDismiss = {
                selectedFood = null
                quantityText = "100"
                showError = false
            },
            onConfirm = {
                val normalized = quantityText.replace(',', '.').toDoubleOrNull()
                if (normalized == null || normalized <= 0.0) {
                    showError = true
                    return@QuantityDialog
                }
                onAlimentoSelected(selectedFood!!, normalized, selectedFood!!.unitBase)
                selectedFood = null
                quantityText = "100"
                showError = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                text = stringResource(R.string.select_food_instruction, stringResource(mealType.stringRes)),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            DockedSearchBar(
                query = searchQuery,
                onQueryChange = foodViewModel::onSearchQueryChange,
                onSearch = { isSearchActive = false },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(R.string.search_food)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.search_food)) }
            ) {
            }
            if (alimentosUiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            alimentosUiState.errorMessage?.let { messageRes ->
                Text(
                    text = stringResource(id = messageRes),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            LazyColumn {
                items(
                    items = alimentosUiState.foods,
                    key = { it.id }
                ) { alimento ->
                    com.eliaskrr.fitmacros.ui.AlimentoItem(
                        food = alimento,
                        onClick = {
                            selectedFood = alimento
                            quantityText = formatQuantity(alimento.amountBase)
                        }
                    )
                }
            }
        }
    }
}

private fun formatQuantity(value: Double): String {
    return BigDecimal.valueOf(value)
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuantityDialog(
    food: Food,
    mealType: MealType,
    quantityText: String,
    onQuantityChange: (String) -> Unit,
    showError: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.select_quantity_for, food.name))
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
                Text(
                    text = stringResource(
                        R.string.unit_label_with_value,
                        stringResource(food.unitBase.labelRes)
                    )
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
