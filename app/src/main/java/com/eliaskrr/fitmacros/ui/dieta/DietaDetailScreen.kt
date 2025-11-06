package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextButton
import androidx.annotation.StringRes
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.domain.MacroCalculationResult
import com.eliaskrr.fitmacros.domain.MissingField
import com.eliaskrr.fitmacros.ui.components.SelectionActionBar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietaDetailScreen(viewModel: DietaDetailViewModel, onAddAlimentoClick: (MealType) -> Unit, onNavigateUp: () -> Unit) {
    val nutrientGoals by viewModel.nutrientGoals.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    uiState.editQuantityState?.let { editState ->
        EditQuantityDialog(
            state = editState,
            onQuantityChange = viewModel::updateEditQuantity,
            onDismiss = viewModel::dismissEditQuantity,
            onConfirm = viewModel::confirmEditQuantity
        )
    }

    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                SelectionActionBar(
                    selectedCount = uiState.selectedCount,
                    onClearSelection = viewModel::clearSelection,
                    onDeleteSelected = viewModel::deleteSelected
                )
            } else {
                TopAppBar(
                    title = { Text("Detalle de Dieta") }, // TODO: Poner el nombre de la dieta real
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                )
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it).padding(16.dp)) {
            item {
                RemainingNutrients(nutrientGoals)
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                MealSection(
                    mealType = MealType.BREAKFAST,
                    viewModel = viewModel,
                    uiState = uiState,
                    onAddAlimentoClick = { onAddAlimentoClick(MealType.BREAKFAST) }
                )
            }
            item {
                MealSection(
                    mealType = MealType.LUNCH,
                    viewModel = viewModel,
                    uiState = uiState,
                    onAddAlimentoClick = { onAddAlimentoClick(MealType.LUNCH) }
                )
            }
            item {
                MealSection(
                    mealType = MealType.AFTERNOON_SNACK,
                    viewModel = viewModel,
                    uiState = uiState,
                    onAddAlimentoClick = { onAddAlimentoClick(MealType.AFTERNOON_SNACK) }
                )
            }
            item {
                MealSection(
                    mealType = MealType.DINNER,
                    viewModel = viewModel,
                    uiState = uiState,
                    onAddAlimentoClick = { onAddAlimentoClick(MealType.DINNER) }
                )
            }
        }
    }
}

@Composable
fun RemainingNutrients(result: MacroCalculationResult) {
    Column {
        Text(
            text = stringResource(R.string.remaining_nutrients),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        when (result) {
            is MacroCalculationResult.Success -> {
                val data = result.data
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    NutrientColumn(value = data.carbGoal.toString(), label = stringResource(R.string.carbohydrates))
                    NutrientColumn(value = data.fatGoal.toString(), label = stringResource(R.string.fats))
                    NutrientColumn(value = data.proteinGoal.toString(), label = stringResource(R.string.proteins))
                    NutrientColumn(value = data.calorieGoal.toString(), label = stringResource(R.string.calories))
                }
            }

            is MacroCalculationResult.MissingData -> {
                MissingDataNotice(result.missingFields)
            }

            MacroCalculationResult.Idle -> MissingDataNotice(emptyList())
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
private fun MissingDataNotice(missingFields: List<MissingField>) {
    val context = LocalContext.current
    val message = if (missingFields.isEmpty()) {
        stringResource(R.string.missing_user_data_generic)
    } else {
        val joinedFields = remember(missingFields, context) {
            missingFields.joinToString(", ") { field -> context.getString(field.labelRes()) }
        }
        stringResource(R.string.missing_user_data, joinedFields)
    }
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@StringRes
private fun MissingField.labelRes(): Int = when (this) {
    MissingField.WEIGHT -> R.string.missing_field_weight
    MissingField.HEIGHT -> R.string.missing_field_height
    MissingField.BIRTH_DATE -> R.string.missing_field_birth_date
    MissingField.SEX -> R.string.missing_field_sex
    MissingField.ACTIVITY_LEVEL -> R.string.missing_field_activity_level
    MissingField.GOAL -> R.string.missing_field_goal
}

@Composable
fun MealSection(
    mealType: MealType,
    viewModel: DietaDetailViewModel,
    uiState: DietaDetailUiState,
    onAddAlimentoClick: () -> Unit
) {
    val mealData by viewModel.getMealData(mealType).collectAsState()
    val selectedIds = uiState.selectedItems[mealType].orEmpty()

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(mealType.stringRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = mealData.totalCalorias.roundToInt().toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        MealMacrosSummary(
            proteinas = mealData.totalProteinas,
            carbos = mealData.totalCarbos,
            grasas = mealData.totalGrasas
        )

        if (mealData.items.isEmpty()) {
            Text(
                text = stringResource(R.string.meal_without_foods),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            mealData.items.forEach { item ->
                val isSelected = selectedIds.contains(item.alimento.id)
                AlimentoInDietaItem(
                    item = item,
                    isSelected = isSelected,
                    selectionMode = uiState.isSelectionMode,
                    onClick = {
                        if (uiState.isSelectionMode) {
                            viewModel.toggleSelection(mealType, item.alimento.id)
                        } else {
                            viewModel.startEditQuantity(mealType, item)
                        }
                    },
                    onLongPress = { viewModel.toggleSelection(mealType, item.alimento.id) }
                )
            }
        }

        Text(
            text = stringResource(R.string.add_alimento).uppercase(),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(onClick = onAddAlimentoClick)
                .padding(vertical = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditQuantityDialog(
    state: EditQuantityState,
    onQuantityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.edit_quantity_for, state.nombre))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = state.quantityText,
                    onValueChange = onQuantityChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.quantity)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    isError = state.showError,
                    supportingText = if (state.showError) {
                        { Text(stringResource(R.string.invalid_quantity)) }
                    } else null
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        R.string.unit_label_with_value,
                        stringResource(state.unidad.labelRes)
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun MealMacrosSummary(proteinas: Double, carbos: Double, grasas: Double) {
    val summaryText = stringResource(
        R.string.meal_macros_summary,
        proteinas,
        carbos,
        grasas
    )
    Text(
        text = summaryText,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlimentoInDietaItem(
    item: MealItem,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val unitAbbreviation = stringResource(item.unidad.shortLabelRes)
    val quantityText = stringResource(R.string.quantity_with_unit, item.cantidad, unitAbbreviation)
    val macrosDetail = stringResource(
        R.string.food_macros_detail,
        item.proteinas,
        item.carbos,
        item.grasas
    )
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(onClick = onClick, onLongClick = onLongPress)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.alimento.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = quantityText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = if (selectionMode) 12.dp else 0.dp)) {
                Text(
                    text = "${item.calorias.roundToInt()} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = macrosDetail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selectionMode) {
                val icon = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
