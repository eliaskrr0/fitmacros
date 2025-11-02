package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.domain.CalculationResult
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun DietaDetailScreen(viewModel: DietaDetailViewModel) {
    val nutrientGoals by viewModel.nutrientGoals.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val addDialogMealType = remember { mutableStateOf<MealType?>(null) }

    Scaffold {
        LazyColumn(modifier = Modifier.padding(it).padding(16.dp)) {
            item {
                RemainingNutrients(
                    nutrientGoals = nutrientGoals,
                    totals = uiState.totals
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            items(MealType.values()) { mealType ->
                MealSection(
                    mealName = mealTypeLabel(mealType),
                    entries = uiState.meals[mealType] ?: emptyList(),
                    onAddClick = { addDialogMealType.value = mealType },
                    onRemoveClick = { viewModel.removeFood(it) }
                )
            }
        }
    }

    addDialogMealType.value?.let { mealType ->
        AddFoodDialog(
            mealType = mealType,
            availableFoods = uiState.availableFoods,
            onDismiss = { addDialogMealType.value = null },
            onAdd = { alimentoId, servings ->
                viewModel.addFoodToMeal(alimentoId, mealType, servings)
                addDialogMealType.value = null
            }
        )
    }
}

@Composable
fun RemainingNutrients(nutrientGoals: CalculationResult, totals: NutrientTotals) {
    val carbRemaining = max(0, nutrientGoals.carbGoal - totals.carbs.roundToInt())
    val fatRemaining = max(0, nutrientGoals.fatGoal - totals.fats.roundToInt())
    val proteinRemaining = max(0, nutrientGoals.proteinGoal - totals.proteins.roundToInt())
    val calorieRemaining = max(0, nutrientGoals.calorieGoal - totals.calories.roundToInt())
    Column {
        Text(
            text = stringResource(R.string.remaining_nutrients),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            NutrientColumn(value = carbRemaining.toString(), label = stringResource(R.string.carbohydrates))
            NutrientColumn(value = fatRemaining.toString(), label = stringResource(R.string.fats))
            NutrientColumn(value = proteinRemaining.toString(), label = stringResource(R.string.proteins))
            NutrientColumn(value = calorieRemaining.toString(), label = stringResource(R.string.calories))
        }
    }
}

@Composable
fun NutrientColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 12.sp)
    }
}

@Composable
fun MealSection(
    mealName: String,
    entries: List<DietaMealEntryUi>,
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = mealName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            val totalCalories = entries.sumOf { it.calories }
            Text(
                text = totalCalories.roundToInt().toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        if (entries.isEmpty()) {
            Text(
                text = stringResource(R.string.no_foods_added),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            entries.forEach { entry ->
                MealEntryRow(entry = entry, onRemoveClick = onRemoveClick)
            }
        }
        TextButton(onClick = onAddClick) {
            Text(stringResource(R.string.add_alimento))
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
    }
}

@Composable
private fun MealEntryRow(entry: DietaMealEntryUi, onRemoveClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.alimentoName, fontWeight = FontWeight.SemiBold)
            Text(
                text = stringResource(
                    R.string.food_macros_preview,
                    entry.servings,
                    entry.proteins.roundToInt(),
                    entry.carbos.roundToInt(),
                    entry.fats.roundToInt(),
                    entry.calories.roundToInt()
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = { onRemoveClick(entry.entryId) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete_alimento))
        }
    }
}

@Composable
private fun AddFoodDialog(
    mealType: MealType,
    availableFoods: List<Alimento>,
    onDismiss: () -> Unit,
    onAdd: (Int, Double) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedAlimentoId by remember { mutableStateOf<Int?>(null) }
    var servingsText by remember { mutableStateOf("1") }

    val filteredFoods = remember(searchQuery, availableFoods) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            availableFoods
        } else {
            availableFoods.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.add_food_to_meal, mealTypeLabel(mealType))) },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(R.string.search_food)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                    items(filteredFoods) { alimento ->
                        val isSelected = selectedAlimentoId == alimento.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        selectedAlimentoId = alimento.id
                                    }
                                )
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = null)
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(text = alimento.nombre, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = stringResource(
                                        R.string.food_macros_preview,
                                        1.0,
                                        alimento.proteinas.roundToInt(),
                                        alimento.carbos.roundToInt(),
                                        alimento.grasas.roundToInt(),
                                        alimento.calorias.roundToInt()
                                    ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = servingsText,
                    onValueChange = { servingsText = it },
                    label = { Text(stringResource(R.string.servings_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            val servings = servingsText.toDoubleOrNull()
            TextButton(
                enabled = selectedAlimentoId != null && servings != null && servings > 0,
                onClick = {
                    val servingsValue = servings ?: return@TextButton
                    selectedAlimentoId?.let { alimentoId ->
                        onAdd(alimentoId, servingsValue)
                    }
                }
            ) {
                Text(stringResource(R.string.add_to_meal))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun mealTypeLabel(mealType: MealType): String {
    return when (mealType) {
        MealType.BREAKFAST -> stringResource(R.string.breakfast)
        MealType.LUNCH -> stringResource(R.string.lunch)
        MealType.AFTERNOON_SNACK -> stringResource(R.string.afternoon_snack)
        MealType.DINNER -> stringResource(R.string.dinner)
    }
}
