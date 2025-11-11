package com.eliaskrr.fitmacros.ui.profile

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.domain.MacroCalculationResult
import com.eliaskrr.fitmacros.domain.MissingField
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.Dimens
import com.eliaskrr.fitmacros.ui.theme.NutrientColors
import com.eliaskrr.fitmacros.ui.theme.TextCardColor
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onEditClick: () -> Unit) {
    val userData by viewModel.userData.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()
    val locale = remember { Locale.getDefault() }
    val dateFormatter = remember(locale) { SimpleDateFormat("ddMMyyyy", locale) }
    val userAge = userData.fechaNacimiento.toMillis(dateFormatter)?.let { ageInYears(it) }?.takeIf { it in 0..100 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.Large)
            .verticalScroll(rememberScrollState()), // Hacemos la columna scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = BackgroundCard,
                    contentColor = TextCardColor
                )
            ) {
                Column(modifier = Modifier.padding(Dimens.Large)) {
                    Text(stringResource(R.string.personal_data), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Dimens.Large))
                    ProfileDataRow(label = stringResource(R.string.user_name), value = userData.name.ifBlank { stringResource(R.string.placeholder_nodata) })
                    ProfileDataRow(label = stringResource(R.string.user_sex), value = userData.sexo.ifBlank { stringResource(R.string.placeholder_nodata) })
                    ProfileDataRow(
                        label = stringResource(R.string.user_age),
                        value = userAge?.let { stringResource(R.string.user_age_value, it) }
                            ?: stringResource(R.string.placeholder_nodata)
                    )
                    ProfileDataRow(label = stringResource(R.string.user_height), value = "${userData.altura.ifBlank { stringResource(R.string.placeholder_nodata) }} ${stringResource(R.string.unit_cm)}")
                    ProfileDataRow(label = stringResource(R.string.user_weight), value = "${userData.peso.ifBlank { stringResource(R.string.placeholder_nodata) }} ${stringResource(R.string.unit_kg)}")
                    ProfileDataRow(label = stringResource(R.string.user_target), value = userData.objetivo.ifBlank { stringResource(R.string.placeholder_nodata) })
                    ProfileDataRow(label = stringResource(R.string.user_activity_level), value = userData.activityRate.ifBlank { stringResource(R.string.placeholder_nodata) })
                }
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_data),
                    tint = TextCardColor
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.ExtraLarge))

        when (val result = calculationResult) {
            is MacroCalculationResult.Success -> {
                CaloriesCard(
                    calorieGoal = result.data.calorieGoal,
                    tdee = result.data.tdee
                )
            }

            is MacroCalculationResult.MissingData -> {
                MissingDataNotice(result.missingFields)
            }

            MacroCalculationResult.Idle -> MissingDataNotice(emptyList())
        }
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Medium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp, maxLines = 1)
    }
}

@Composable
fun CaloriesCard(calorieGoal: Int, tdee: Int) {
    val remainingCalories = calorieGoal // Asumimos 0 consumido por ahora

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard,
            contentColor = TextCardColor
        )
    ) {
        Column(modifier = Modifier.padding(Dimens.Large)) {
            Text(stringResource(R.string.calories), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Dimens.Large))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val progress = if (calorieGoal > 0) remainingCalories.toFloat() / calorieGoal.toFloat() else 0f
                    CircularProgress(progress = progress, color = NutrientColors.Calories, size = 120.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$remainingCalories", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.calories_remaining), fontSize = 12.sp)
                    }
                }
                Column {
                    InfoRowWithIcon(icon = Icons.Outlined.TrackChanges, label = stringResource(R.string.maintenance_calories), value = "$tdee")
                    InfoRowWithIcon(icon = Icons.Outlined.Flag, label = stringResource(R.string.target_calories), value = "$calorieGoal")
                    InfoRowWithIcon(icon = Icons.Outlined.Restaurant, label = stringResource(R.string.food_calories), value = "0")
                }
            }
        }
    }
}

@Composable
private fun MissingDataNotice(missingFields: List<MissingField>) {
    val context = LocalContext.current
    val message = remember(missingFields, context) {
        if (missingFields.isEmpty()) {
            context.getString(R.string.missing_user_data_generic)
        } else {
            val joinedFields = missingFields.joinToString(", ") { field ->
                context.getString(field.labelRes())
            }
            context.getString(R.string.missing_user_data, joinedFields)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard,
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(Dimens.Large),
            style = MaterialTheme.typography.bodyMedium
        )
    }
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
fun NutrientProgressIndicator(label: String, consumed: Int, goal: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(Dimens.Medium))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            CircularProgress(progress = (if (goal > 0) consumed.toFloat() / goal.toFloat() else 0f), color = color)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$consumed", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("/ $goal g", fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(Dimens.Small))
        Text(stringResource(R.string.grams_to_go, goal - consumed), fontSize = 12.sp)
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
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = Dimens.Small)) {
        Icon(icon, contentDescription = label, tint = TextCardColor.copy(alpha = 0.9f))
        Spacer(modifier = Modifier.width(Dimens.Medium))
        Text("$label: ", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}
