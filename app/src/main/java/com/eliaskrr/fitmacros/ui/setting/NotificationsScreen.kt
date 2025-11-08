package com.eliaskrr.fitmacros.ui.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateUp: () -> Unit,
    viewModel: NotificationsViewModel = viewModel()
) {
    val reminderToEdit = viewModel.editingReminder
    if (viewModel.showTimePicker && reminderToEdit != null) {
        TimePickerDialog(
            initialTime = when (reminderToEdit) {
                ReminderType.BREAKFAST -> viewModel.breakfastTime
                ReminderType.LUNCH -> viewModel.lunchTime
                ReminderType.DINNER -> viewModel.dinnerTime
                ReminderType.WEIGH_IN -> viewModel.weighInTime
            },
            onDismiss = { viewModel.onTimePickerDismissed() },
            onConfirm = { newTime ->
                viewModel.onTimeSelected(newTime)
                viewModel.onTimePickerDismissed()
            }
        )
    }

    if (viewModel.showDayPicker) {
        DayOfWeekPickerDialog(
            onDismiss = { viewModel.onDayPickerDismissed() },
            onConfirm = { newDay ->
                viewModel.onDaySelected(newDay)
                viewModel.onDayPickerDismissed()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notificaciones y Recordatorios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NotificationCard(
                    title = "Recordatorios de Comidas",
                    description = "Recibir notificaciones para registrar tus comidas."
                ) {
                    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
                    Column {
                        ReminderTimeSelector(label = "Desayuno", time = viewModel.breakfastTime.format(timeFormatter)) {
                            viewModel.onTimePickerRequested(ReminderType.BREAKFAST)
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        ReminderTimeSelector(label = "Almuerzo", time = viewModel.lunchTime.format(timeFormatter)) {
                            viewModel.onTimePickerRequested(ReminderType.LUNCH)
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        ReminderTimeSelector(label = "Cena", time = viewModel.dinnerTime.format(timeFormatter)) {
                            viewModel.onTimePickerRequested(ReminderType.DINNER)
                        }
                    }
                }
            }
            item {
                NotificationCard(
                    title = "Recordatorio de Pesaje",
                    description = "Recibir una notificación para registrar tu peso."
                ) {
                    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
                    Column {
                        ReminderTimeSelector(
                            label = "Día",
                            time = viewModel.weighInDay.getDisplayName(TextStyle.FULL, Locale("es"))
                        ) {
                            viewModel.onDayPickerRequested()
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        ReminderTimeSelector(
                            label = "Hora",
                            time = viewModel.weighInTime.format(timeFormatter)
                        ) {
                            viewModel.onTimePickerRequested(ReminderType.WEIGH_IN)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(title: String, description: String, content: @Composable () -> Unit) {
    var isEnabled by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEnabled = !isEnabled }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.alpha(0.7f))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
            }
            AnimatedVisibility(visible = isEnabled) {
                Column {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    content()
                }
            }
        }
    }
}

@Composable
private fun ReminderTimeSelector(label: String, time: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = time, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
