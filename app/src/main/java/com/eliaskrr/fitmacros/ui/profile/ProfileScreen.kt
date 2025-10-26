package com.eliaskrr.fitmacros.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onEditClick: () -> Unit) {
    val userData by viewModel.userData.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Hacemos la columna scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos Personales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileDataRow(label = "Nombre", value = userData.nombre.ifBlank { "-" })
                    ProfileDataRow(label = "Sexo", value = userData.sexo.ifBlank { "-" })
                    ProfileDataRow(label = "Altura", value = "${userData.altura.ifBlank { "-" }} cm")
                    ProfileDataRow(label = "Peso", value = "${userData.peso.ifBlank { "-" }} kg")
                    ProfileDataRow(label = "Objetivo", value = userData.objetivo.ifBlank { "-" })
                    ProfileDataRow(label = "Nivel de Actividad", value = userData.activityRate.ifBlank { "-" })
                }
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Datos")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        MacronutrientsCard(
            carbGoal = calculationResult.carbGoal,
            fatGoal = calculationResult.fatGoal,
            proteinGoal = calculationResult.proteinGoal
        )

        Spacer(modifier = Modifier.height(16.dp))

        CaloriesCard(
            calorieGoal = calculationResult.calorieGoal,
            tdee = calculationResult.tdee
        )
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp, maxLines = 1)
    }
}

@Composable
fun MacronutrientsCard(carbGoal: Int, fatGoal: Int, proteinGoal: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Macronutrientes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NutrientProgressIndicator(label = "Carbohidratos", consumed = 0, goal = carbGoal, color = Color(0xFF4CAF50))
                NutrientProgressIndicator(label = "Grasas", consumed = 0, goal = fatGoal, color = Color(0xFF9C27B0))
                NutrientProgressIndicator(label = "Proteínas", consumed = 0, goal = proteinGoal, color = Color(0xFFE57373))
            }
        }
    }
}

@Composable
fun CaloriesCard(calorieGoal: Int, tdee: Int) {
    val remainingCalories = calorieGoal // Asumimos 0 consumido por ahora

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Calorías", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Restantes = Objetivo - Alimentos + Ejercicio", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val progress = if (calorieGoal > 0) remainingCalories.toFloat() / calorieGoal.toFloat() else 0f
                    CircularProgress(progress = progress, color = MaterialTheme.colorScheme.primary, size = 120.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$remainingCalories", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Restantes", fontSize = 12.sp)
                    }
                }
                Column {
                    InfoRowWithIcon(icon = Icons.Outlined.TrackChanges, label = "Mantenimiento", value = "$tdee")
                    InfoRowWithIcon(icon = Icons.Outlined.Flag, label = "Objetivo", value = "$calorieGoal")
                    InfoRowWithIcon(icon = Icons.Outlined.Restaurant, label = "Alimentos", value = "0")
                    InfoRowWithIcon(icon = Icons.Outlined.LocalFireDepartment, label = "Ejercicio", value = "0")
                }
            }
        }
    }
}

@Composable
fun NutrientProgressIndicator(label: String, consumed: Int, goal: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            CircularProgress(progress = (if (goal > 0) consumed.toFloat() / goal.toFloat() else 0f), color = color)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$consumed", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("/ $goal g", fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("${goal - consumed} g faltan", fontSize = 12.sp)
    }
}

@Composable
fun CircularProgress(progress: Float, color: Color, size: Dp = 80.dp, strokeWidth: Float = 8f) {
    Canvas(modifier = Modifier.size(size)) {
        val arcRadius = this.size.minDimension / 2 - strokeWidth / 2
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun InfoRowWithIcon(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: ", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}
