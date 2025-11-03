package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.MealType
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietaDetailScreen(viewModel: DietaDetailViewModel, onAddAlimentoClick: (MealType) -> Unit, onNavigateUp: () -> Unit) {
    val nutrientGoals by viewModel.nutrientGoals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Dieta") }, // TODO: Poner el nombre de la dieta real
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it).padding(16.dp)) {
            item {
                RemainingNutrients(
                    carbGoal = nutrientGoals.carbGoal,
                    fatGoal = nutrientGoals.fatGoal,
                    proteinGoal = nutrientGoals.proteinGoal,
                    calorieGoal = nutrientGoals.calorieGoal
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                MealSection(mealType = MealType.BREAKFAST, viewModel = viewModel, onAddAlimentoClick = { onAddAlimentoClick(MealType.BREAKFAST) })
            }
            item {
                MealSection(mealType = MealType.LUNCH, viewModel = viewModel, onAddAlimentoClick = { onAddAlimentoClick(MealType.LUNCH) })
            }
            item {
                MealSection(mealType = MealType.AFTERNOON_SNACK, viewModel = viewModel, onAddAlimentoClick = { onAddAlimentoClick(MealType.AFTERNOON_SNACK) })
            }
            item {
                MealSection(mealType = MealType.DINNER, viewModel = viewModel, onAddAlimentoClick = { onAddAlimentoClick(MealType.DINNER) })
            }
        }
    }
}

@Composable
fun RemainingNutrients(carbGoal: Int, fatGoal: Int, proteinGoal: Int, calorieGoal: Int) {
    Column {
        Text(
            text = stringResource(R.string.remaining_nutrients),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            NutrientColumn(value = carbGoal.toString(), label = stringResource(R.string.carbohydrates))
            NutrientColumn(value = fatGoal.toString(), label = stringResource(R.string.fats))
            NutrientColumn(value = proteinGoal.toString(), label = stringResource(R.string.proteins))
            NutrientColumn(value = calorieGoal.toString(), label = stringResource(R.string.calories))
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
    mealType: MealType,
    viewModel: DietaDetailViewModel,
    onAddAlimentoClick: () -> Unit
) {
    val mealData by viewModel.getMealData(mealType).collectAsState()

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
                AlimentoInDietaItem(item)
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

@Composable
fun AlimentoInDietaItem(item: MealItem) {
    val unitAbbreviation = stringResource(item.unidad.shortLabelRes)
    val quantityText = stringResource(R.string.quantity_with_unit, item.cantidad, unitAbbreviation)
    val macrosDetail = stringResource(
        R.string.food_macros_detail,
        item.proteinas,
        item.carbos,
        item.grasas
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.alimento.nombre, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = quantityText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${item.calorias.roundToInt()} kcal",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = macrosDetail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
