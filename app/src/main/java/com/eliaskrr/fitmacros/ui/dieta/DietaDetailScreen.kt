package com.eliaskrr.fitmacros.ui.dieta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun DietaDetailScreen(viewModel: DietaDetailViewModel) {
    val nutrientGoals by viewModel.nutrientGoals.collectAsState()

    Scaffold {
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
                MealSection(mealName = stringResource(R.string.breakfast))
            }
            item {
                MealSection(mealName = stringResource(R.string.lunch))
            }
            item {
                MealSection(mealName = stringResource(R.string.afternoon_snack))
            }
            item {
                MealSection(mealName = stringResource(R.string.dinner))
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
fun MealSection(mealName: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = mealName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(text = "0", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        // Aquí iría la lista de alimentos de esta comida
        TextButton(onClick = { /* TODO */ }) {
            Text(stringResource(R.string.add_alimento))
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
    }
}
