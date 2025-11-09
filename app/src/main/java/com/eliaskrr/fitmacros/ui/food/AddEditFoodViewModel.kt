package com.eliaskrr.fitmacros.ui.food

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit
import com.eliaskrr.fitmacros.data.repository.nutrition.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class AlimentoUiState(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val brand: String = "",
    val proteins: String = "",
    val carbs: String = "",
    val fats: String = "",
    val amountBase: String = "100",
    val unitBase: QuantityUnit = QuantityUnit.GRAMS,
    val calories: String = "0",
    val creationDate: Long? = null,
    val updateDate: Long? = null,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)

@HiltViewModel
class AddEditAlimentoViewModel @Inject constructor(
    private val repository: FoodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlimentoUiState())
    val uiState: StateFlow<AlimentoUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddEditAlimentoEvent>()
    val events: SharedFlow<AddEditAlimentoEvent> = _events.asSharedFlow()

    private val foodId: Int? = savedStateHandle["foodId"]

    private val decimalSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
    private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        isGroupingUsed = false
    }

    init {
        // El valor por defecto de foodId es -1, así que solo cargamos si es un ID válido
        if (foodId != null && foodId != -1) {
            Log.d(TAG, "Inicializando edición para alimento con id $foodId")
            loadAlimento(foodId)
        }
    }

    private fun loadAlimento(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val alimento = repository.getById(id).first()
                if (alimento != null) {
                    Log.d(TAG, "Alimento cargado para edición: ${alimento.name} (id=${alimento.id})")
                    val caloriasCalculadas = calculateCalories(alimento.proteins, alimento.carbs, alimento.fats)
                    _uiState.update {
                        it.copy(
                            id = alimento.id,
                            name = alimento.name,
                            price = alimento.price?.toString() ?: "",
                            brand = alimento.brand ?: "",
                            proteins = alimento.proteins.toString(),
                            carbs = alimento.carbs.toString(),
                            fats = alimento.fats.toString(),
                            amountBase = formatQuantity(alimento.amountBase),
                            unitBase = alimento.unitBase,
                            calories = formatCalories(caloriasCalculadas),
                            creationDate = alimento.creationDate,
                            updateDate = alimento.updateDate,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    Log.w(TAG, "No se encontró el alimento con id $id")
                    _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_loading_fodd) }
                    _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_loading_fodd))
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error cargando alimento con id $id", ex)
                _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_loading_fodd) }
                _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_loading_fodd))
            }
        }
    }

    fun onValueChange(
        name: String? = null,
        price: String? = null,
        brand: String? = null,
        proteins: String? = null,
        carbs: String? = null,
        fats: String? = null,
        amountBase: String? = null,
        unitBase: QuantityUnit? = null
    ) {
        _uiState.update { currentState ->
            val updatedProteinas = proteins?.let { normalizeDecimalInput(it) } ?: currentState.proteins
            val updatedCarbos = carbs?.let { normalizeDecimalInput(it) } ?: currentState.carbs
            val updatedGrasas = fats?.let { normalizeDecimalInput(it) } ?: currentState.fats
            val updatedPrecio = price?.let { normalizeDecimalInput(it, allowNegative = false) } ?: currentState.price
            val updatedCantidadBase = amountBase?.let { normalizeDecimalInput(it) } ?: currentState.amountBase

            val calculatedCalories = calculateCalories(updatedProteinas, updatedCarbos, updatedGrasas)

            currentState.copy(
                name = name ?: currentState.name,
                price = updatedPrecio,
                brand = brand ?: currentState.brand,
                proteins = updatedProteinas,
                carbs = updatedCarbos,
                fats = updatedGrasas,
                amountBase = updatedCantidadBase,
                unitBase = unitBase ?: currentState.unitBase,
                calories = formatCalories(calculatedCalories),
                errorMessage = null
            )
        }
    }

    fun saveAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            val isNewAlimento = foodId == null || foodId == -1

            val caloriasCalculadas = calculateCalories(state.proteins, state.carbs, state.fats)

            val food = if (isNewAlimento) {
                Food(
                    id = 0,
                    name = state.name,
                    price = parseDecimal(state.price)?.toDouble(),
                    brand = state.brand.ifEmpty { null },
                    proteins = parseMacroInput(state.proteins),
                    carbs = parseMacroInput(state.carbs),
                    fats = parseMacroInput(state.fats),
                    amountBase = parseQuantity(state.amountBase),
                    unitBase = state.unitBase,
                    calories = caloriasCalculadas
                )
            } else {
                val now = System.currentTimeMillis()
                Food(
                    id = state.id,
                    name = state.name,
                    price = parseDecimal(state.price)?.toDouble(),
                    brand = state.brand.ifEmpty { null },
                    proteins = parseMacroInput(state.proteins),
                    carbs = parseMacroInput(state.carbs),
                    fats = parseMacroInput(state.fats),
                    amountBase = parseQuantity(state.amountBase),
                    unitBase = state.unitBase,
                    calories = caloriasCalculadas,
                    creationDate = state.creationDate ?: now,
                    updateDate = now
                )
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSaved = false) }
            try {
                if (isNewAlimento) {
                    Log.d(TAG, "Insertando nuevo alimento ${food.name}")
                    repository.insert(food)
                } else {
                    Log.d(TAG, "Actualizando alimento ${food.name} (id=${food.id})")
                    repository.update(food)
                }
                Log.i(TAG, "Alimento guardado correctamente: ${food.name}")
                _uiState.update {
                    it.copy(
                        isSaved = true,
                        isLoading = false,
                        errorMessage = null,
                        creationDate = food.creationDate,
                        updateDate = food.updateDate
                    )
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error al guardar alimento ${food.name}", ex)
                _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_saving_food) }
                _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_saving_food))
            }
        }
    }

    fun deleteAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.id != 0) {
                val amountBase = parseQuantity(state.amountBase)
                val foodToDelete = Food(
                    id = state.id,
                    name = state.name,
                    proteins = parseMacroInput(state.proteins),
                    carbs = parseMacroInput(state.carbs),
                    fats = parseMacroInput(state.fats),
                    amountBase = amountBase,
                    unitBase = state.unitBase,
                    calories = calculateCalories(state.proteins, state.carbs, state.fats)
                )
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    Log.d(TAG, "Eliminando alimento ${foodToDelete.name} (id=${foodToDelete.id})")
                    repository.delete(foodToDelete)
                    Log.i(TAG, "Alimento eliminado correctamente: ${foodToDelete.name}")
                    _uiState.update { it.copy(isSaved = true, isLoading = false, errorMessage = null) }
                } catch (ex: Exception) {
                    Log.e(TAG, "Error al eliminar alimento ${foodToDelete.name} (id=${foodToDelete.id})", ex)
                    _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_deleting_food) }
                    _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_deleting_food))
                }
            }
        }
    }

    companion object {
        private const val TAG = "AddEditAlimentoVM"
    }

    sealed interface AddEditAlimentoEvent {
        data class ShowMessage(val messageRes: Int) : AddEditAlimentoEvent
    }

    private fun calculateCalories(proteins: String, carbs: String, fats: String): Double {
        val proteinasDouble = parseMacroInput(proteins)
        val carbosDouble = parseMacroInput(carbs)
        val grasasDouble = parseMacroInput(fats)
        return calculateCalories(proteinasDouble, carbosDouble, grasasDouble)
    }

    private fun calculateCalories(proteins: Double, carbs: Double, fats: Double): Double {
        return (proteins * 4) + (carbs * 4) + (fats * 9)
    }

    private fun parseMacroInput(value: String): Double {
        return parseDecimal(value)?.takeIf { it >= BigDecimal.ZERO }?.toDouble() ?: 0.0
    }

    private fun formatCalories(calories: Double): String {
        return if (calories == 0.0) {
            "0"
        } else {
            BigDecimal.valueOf(calories)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
        }
    }

    private fun formatQuantity(value: Double): String {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    private fun parseQuantity(value: String): Double {
        val parsed = parseDecimal(value)?.toDouble()
        return if (parsed != null && parsed > 0.0) parsed else 100.0
    }

    private fun normalizeDecimalInput(
        value: String,
        scale: Int = 2,
        allowNegative: Boolean = false
    ): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return ""

        val sanitized = sanitizeSeparators(trimmed)
        if (sanitized.isEmpty()) return ""

        val decimalSeparator = decimalSymbols.decimalSeparator

        if (sanitized.last() == decimalSeparator && isValidNumericPrefix(sanitized.dropLast(1), allowNegative)) {
            return sanitized
        }

        val decimal = parseDecimal(sanitized) ?: return if (isValidNumericPrefix(sanitized, allowNegative)) {
            sanitized
        } else {
            ""
        }

        if (!allowNegative && decimal < BigDecimal.ZERO) return ""

        val normalized = decimal
            .setScale(scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()

        return if (decimalSeparator == '.') {
            normalized
        } else {
            normalized.replace('.', decimalSeparator)
        }
    }

    private fun parseDecimal(value: String): BigDecimal? {
        if (value.isBlank()) return null
        val sanitized = sanitizeSeparators(value)
        if (sanitized.isEmpty()) return null
        val number = runCatching { numberFormat.parse(sanitized) }.getOrNull() ?: return null
        return when (number) {
            is Long -> BigDecimal.valueOf(number.toLong())
            is Int -> BigDecimal.valueOf(number.toLong())
            is Double -> BigDecimal.valueOf(number)
            is Float -> BigDecimal.valueOf(number.toDouble())
            else -> BigDecimal.valueOf(number.toDouble())
        }
    }

    private fun sanitizeSeparators(value: String): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return ""

        val decimalSeparator = decimalSymbols.decimalSeparator
        val groupingSeparator = decimalSymbols.groupingSeparator

        return buildString(trimmed.length) {
            trimmed.forEach { char ->
                when {
                    char == decimalSeparator -> append(char)
                    char == ',' || char == '.' -> append(decimalSeparator)
                    char == groupingSeparator && groupingSeparator != decimalSeparator -> Unit
                    else -> append(char)
                }
            }
        }
    }

    private fun isValidNumericPrefix(value: String, allowNegative: Boolean): Boolean {
        if (value.isEmpty()) return true

        var startIndex = 0
        val minusSign = decimalSymbols.minusSign

        if (value.first() == '-' || value.first() == minusSign) {
            if (!allowNegative) return false
            startIndex = 1
        }

        if (startIndex >= value.length) return false

        for (index in startIndex until value.length) {
            if (!value[index].isDigit()) return false
        }

        return true
    }
}
