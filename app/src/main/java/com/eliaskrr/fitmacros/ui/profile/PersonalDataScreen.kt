package com.eliaskrr.fitmacros.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.ActivityRate
import com.eliaskrr.fitmacros.data.model.TypeTarget
import com.eliaskrr.fitmacros.data.repository.UserData
import com.eliaskrr.fitmacros.ui.theme.ButtonConfirmColor
import com.eliaskrr.fitmacros.ui.theme.ButtonCancelColor
import com.eliaskrr.fitmacros.ui.theme.Dimens
import com.eliaskrr.fitmacros.ui.theme.DialogBackgroundColor
import com.eliaskrr.fitmacros.ui.theme.TextFieldContainerColor
import com.eliaskrr.fitmacros.ui.theme.TextGeneralColor
import com.eliaskrr.fitmacros.ui.theme.TextCardColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(userData: UserData, onSave: (UserData) -> Unit, onNavigateUp: () -> Unit) {
    val locale = remember { Locale("es") }
    val dateFormatter = remember(locale) { SimpleDateFormat("ddMMyyyy", locale) }
    var nombre by remember { mutableStateOf(userData.nombre) }
    var sexo by remember { mutableStateOf(userData.sexo) }
    var fechaNacimiento by remember {
        mutableStateOf(
            userData.fechaNacimiento.takeIf { rawDate ->
                val millis = rawDate.toMillis(dateFormatter)
                millis != null && isAgeAllowed(millis)
            } ?: ""
        )
    }
    var altura by remember { mutableStateOf(userData.altura) }
    var peso by remember { mutableStateOf(userData.peso) }
    var objetivo by remember { mutableStateOf(userData.objetivo) }
    var activityRate by remember { mutableStateOf(userData.activityRate) }
    val selectableBirthDates = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = isAgeAllowed(utcTimeMillis)
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = fechaNacimiento.toMillis(dateFormatter),
        selectableDates = selectableBirthDates
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var initialDateOnOpen by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(fechaNacimiento) {
        val selectedMillis = fechaNacimiento.toMillis(dateFormatter)
        if (selectedMillis != null && selectedMillis != datePickerState.selectedDateMillis) {
            datePickerState.selectedDateMillis = selectedMillis
        }
    }

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            initialDateOnOpen = datePickerState.selectedDateMillis
        } else {
            initialDateOnOpen = null
        }
    }

    LaunchedEffect(showDatePicker, datePickerState.selectedDateMillis) {
        if (showDatePicker) {
            val selectedMillis = datePickerState.selectedDateMillis
            if (selectedMillis != null && selectedMillis != initialDateOnOpen) {
                fechaNacimiento = dateFormatter.format(Date(selectedMillis))
                showDatePicker = false
            }
        }
    }

    val datePickerColors = DatePickerDefaults.colors(
        containerColor = DialogBackgroundColor,
        titleContentColor = TextCardColor,
        headlineContentColor = TextCardColor,
        weekdayContentColor = TextCardColor.copy(alpha = 0.75f),
        subheadContentColor = TextCardColor.copy(alpha = 0.75f),
        navigationContentColor = TextCardColor,
        yearContentColor = TextCardColor.copy(alpha = 0.8f),
        disabledYearContentColor = TextCardColor.copy(alpha = 0.3f),
        currentYearContentColor = ButtonConfirmColor,
        selectedYearContentColor = TextGeneralColor,
        selectedYearContainerColor = ButtonConfirmColor,
        dayContentColor = TextCardColor,
        disabledDayContentColor = TextCardColor.copy(alpha = 0.3f),
        selectedDayContentColor = TextGeneralColor,
        selectedDayContainerColor = ButtonConfirmColor,
        todayContentColor = ButtonConfirmColor,
        todayDateBorderColor = ButtonConfirmColor,
        dividerColor = TextCardColor.copy(alpha = 0.2f)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = ButtonConfirmColor)
                ) { Text(stringResource(R.string.accept)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = ButtonCancelColor)
                ) { Text(stringResource(R.string.cancel)) }
            },
            colors = datePickerColors
        ) {
            DatePicker(
                state = datePickerState,
                colors = datePickerColors,
                showModeToggle = false
            )
        }
    }

    val userAge = fechaNacimiento.toMillis(dateFormatter)?.let { birthMillis ->
        ageInYears(birthMillis)?.takeIf { it in 0..100 }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = TextFieldContainerColor,
        unfocusedContainerColor = TextFieldContainerColor,
        disabledContainerColor = TextFieldContainerColor.copy(alpha = 0.6f),
        focusedBorderColor = TextGeneralColor.copy(alpha = 0.6f),
        unfocusedBorderColor = TextGeneralColor.copy(alpha = 0.3f),
        focusedLabelColor = TextGeneralColor.copy(alpha = 0.9f),
        unfocusedLabelColor = TextGeneralColor.copy(alpha = 0.7f),
        focusedTextColor = TextGeneralColor,
        unfocusedTextColor = TextGeneralColor,
        disabledTextColor = TextGeneralColor.copy(alpha = 0.6f),
        cursorColor = TextGeneralColor
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.personal_data)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(Dimens.Large)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.user_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            val sexOptions = listOf(stringResource(R.string.sex_male), stringResource(R.string.sex_female))
            var expandedSex by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedSex,
                onExpandedChange = { expandedSex = !expandedSex }
            ) {
                OutlinedTextField(
                    value = sexo,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.user_sex)) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSex) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedSex,
                    onDismissRequest = { expandedSex = false }
                ) {
                    sexOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                sexo = option
                                expandedSex = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {},
                label = { Text(stringResource(R.string.birthdate)) },
                placeholder = { Text(stringResource(R.string.birthdate_placeholder)) },
                visualTransformation = DateVisualTransformation(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = stringResource(R.string.calendar))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = textFieldColors
            )
            if (userAge != null) {
                Spacer(modifier = Modifier.height(Dimens.Small))
                Text(
                    text = stringResource(R.string.user_age_years, userAge),
                    color = TextGeneralColor
                )
            }
            Spacer(modifier = Modifier.height(Dimens.Medium))

            OutlinedTextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text(stringResource(R.string.height_cm)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = sanitizeDecimalInput(it) },
                label = { Text(stringResource(R.string.weight_kg)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            val targetOptions = TypeTarget.entries.map { it.description }
            var expandedTarget by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedTarget,
                onExpandedChange = { expandedTarget = !expandedTarget }
            ) {
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.user_target)) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTarget) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedTarget,
                    onDismissRequest = { expandedTarget = false }
                ) {
                    targetOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                objetivo = option
                                expandedTarget = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            val activityOptions = ActivityRate.entries.map { it.description }
            var expandedActivity by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedActivity,
                onExpandedChange = { expandedActivity = !expandedActivity }
            ) {
                OutlinedTextField(
                    value = activityRate,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.user_activity_level)) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivity) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedActivity,
                    onDismissRequest = { expandedActivity = false }
                ) {
                    activityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                activityRate = option
                                expandedActivity = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.Large))
            
            Button(
                onClick = { onSave(UserData(nombre, sexo, fechaNacimiento, altura, peso, objetivo, activityRate)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonConfirmColor,
                    contentColor = TextGeneralColor
                )
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) {
                if (i < trimmed.length - 1) {
                    out += "/"
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset - 2 <= 8) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

private fun sanitizeDecimalInput(rawInput: String): String {
    if (rawInput.isEmpty()) return ""
    val normalized = rawInput.replace(',', '.')
    val builder = StringBuilder(normalized.length)
    var dotUsed = false

    normalized.forEach { char ->
        when {
            char.isDigit() -> builder.append(char)
            char == '.' && !dotUsed -> {
                builder.append(char)
                dotUsed = true
            }
        }
    }

    return builder.toString()
}
