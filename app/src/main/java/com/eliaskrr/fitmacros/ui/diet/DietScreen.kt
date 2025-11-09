package com.eliaskrr.fitmacros.ui.diet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.entity.nutrition.Diet
import com.eliaskrr.fitmacros.ui.components.SelectionActionBar
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard
import com.eliaskrr.fitmacros.ui.theme.ButtonCancelColor
import com.eliaskrr.fitmacros.ui.theme.ButtonConfirmColor
import com.eliaskrr.fitmacros.ui.theme.DialogBackgroundColor
import com.eliaskrr.fitmacros.ui.theme.DialogTextColor
import com.eliaskrr.fitmacros.ui.theme.DialogTitleColor
import com.eliaskrr.fitmacros.ui.theme.TextCardColor

@Composable
fun DietasScreen(viewModel: DietViewModel, onDietaClick: (Int) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newDietaName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DietViewModel.DietaEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = DialogBackgroundColor,
            titleContentColor = DialogTitleColor,
            textContentColor = DialogTextColor,
            title = { Text(stringResource(R.string.new_diet_title)) },
            text = {
                OutlinedTextField(
                    value = newDietaName,
                    onValueChange = { newDietaName = it },
                    label = { Text(stringResource(R.string.dieta_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundCard,
                        unfocusedContainerColor = BackgroundCard,
                        disabledContainerColor = BackgroundCard,
                        cursorColor = TextCardColor,
                        focusedBorderColor = TextCardColor.copy(alpha = 0.8f),
                        unfocusedBorderColor = TextCardColor.copy(alpha = 0.5f),
                        focusedLabelColor = TextCardColor.copy(alpha = 0.8f),
                        unfocusedLabelColor = TextCardColor.copy(alpha = 0.5f),
                        focusedTextColor = TextCardColor,
                        unfocusedTextColor = TextCardColor
                    )
                )
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonCancelColor)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDietaName.isNotBlank()) {
                            viewModel.insert(Diet(name = newDietaName))
                            newDietaName = ""
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonConfirmColor)
                ) {
                    Text(stringResource(R.string.create))
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_diet_title))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.isSelectionMode) {
                SelectionActionBar(
                    selectedCount = uiState.selectedDietas.size,
                    onClearSelection = viewModel::clearSelection,
                    onDeleteSelected = viewModel::deleteSelected
                )
            }
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            uiState.errorMessage?.let { messageRes ->
                Text(
                    text = stringResource(messageRes),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(uiState.diets) { dieta ->
                    val isSelected = uiState.selectedDietas.contains(dieta.id)
                    DietaItem(
                        diet = dieta,
                        isSelected = isSelected,
                        selectionMode = uiState.isSelectionMode,
                        onClick = {
                            if (uiState.isSelectionMode) {
                                viewModel.toggleSelection(dieta.id)
                            } else {
                                onDietaClick(dieta.id)
                            }
                        },
                        onLongClick = { viewModel.toggleSelection(dieta.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DietaItem(
    diet: Diet,
    isSelected: Boolean = false,
    selectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        BackgroundCard
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        TextCardColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = diet.name,
                modifier = Modifier.weight(1f),
                color = contentColor
            )
            if (selectionMode) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }
}
