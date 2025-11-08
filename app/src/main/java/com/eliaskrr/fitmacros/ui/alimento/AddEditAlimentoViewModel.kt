package com.eliaskrr.fitmacros.ui.alimento

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.QuantityUnit
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
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
import java.util.Locale
import javax.inject.Inject

data class AlimentoUiState(
    val id: Int = 0,
    val nombre: String = "",
    val precio: String = "",
    val marca: String = "",
    val proteinas: String = "",
    val carbos: String = "",
    val grasas: String = "",
    val cantidadBase: String = "100",
    val unidadBase: QuantityUnit = QuantityUnit.GRAMS,
    val calorias: String = "0",
    val detalles: String = "",
    val fechaCreacion: Long? = null,
    val fechaActualizacion: Long? = null,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)

@HiltViewModel
class AddEditAlimentoViewModel @Inject constructor(
    private val repository: AlimentoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlimentoUiState())
    val uiState: StateFlow<AlimentoUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddEditAlimentoEvent>()
    val events: SharedFlow<AddEditAlimentoEvent> = _events.asSharedFlow()

    private val alimentoId: Int? = savedStateHandle["alimentoId"]

    private val decimalSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale("es"))
    init {
        // El valor por defecto de alimentoId es -1, así que solo cargamos si es un ID válido
        if (alimentoId != null && alimentoId != -1) {
            Log.d(TAG, "Inicializando edición para alimento con id $alimentoId")
            loadAlimento(alimentoId)
        }
    }

    private fun loadAlimento(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val alimento = repository.getById(id).first()
                if (alimento != null) {
                    Log.d(TAG, "Alimento cargado para edición: ${alimento.nombre} (id=${alimento.id})")
                    val caloriasCalculadas = calculateCalories(alimento.proteinas, alimento.carbos, alimento.grasas)
                    _uiState.update {
                        it.copy(
                            id = alimento.id,
                            nombre = alimento.nombre,
                            precio = alimento.precio?.toString() ?: "",
                            marca = alimento.marca ?: "",
                            proteinas = alimento.proteinas.toString(),
                            carbos = alimento.carbos.toString(),
                            grasas = alimento.grasas.toString(),
                            cantidadBase = formatQuantity(alimento.cantidadBase),
                            unidadBase = alimento.unidadBase,
                            calorias = formatCalories(caloriasCalculadas),
                            detalles = alimento.detalles ?: "",
                            fechaCreacion = alimento.fechaCreacion,
                            fechaActualizacion = alimento.fechaActualizacion,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    Log.w(TAG, "No se encontró el alimento con id $id")
                    _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_loading_alimento) }
                    _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_loading_alimento))
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error cargando alimento con id $id", ex)
                _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_loading_alimento) }
                _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_loading_alimento))
            }
        }
    }

    fun onValueChange(
        nombre: String? = null,
        precio: String? = null,
        marca: String? = null,
        proteinas: String? = null,
        carbos: String? = null,
        grasas: String? = null,
        cantidadBase: String? = null,
        unidadBase: QuantityUnit? = null,
        detalles: String? = null
    ) {
        _uiState.update { currentState ->
            val updatedProteinas = proteinas ?: currentState.proteinas
            val updatedCarbos = carbos ?: currentState.carbos
            val updatedGrasas = grasas ?: currentState.grasas
            val updatedPrecio = precio ?: currentState.precio
            val updatedCantidadBase = cantidadBase ?: currentState.cantidadBase

            val calculatedCalories = calculateCalories(updatedProteinas, updatedCarbos, updatedGrasas)

            currentState.copy(
                nombre = nombre ?: currentState.nombre,
                precio = updatedPrecio,
                marca = marca ?: currentState.marca,
                proteinas = updatedProteinas,
                carbos = updatedCarbos,
                grasas = updatedGrasas,
                cantidadBase = updatedCantidadBase,
                unidadBase = unidadBase ?: currentState.unidadBase,
                calorias = formatCalories(calculatedCalories),
                detalles = detalles ?: currentState.detalles,
                errorMessage = null
            )
        }
    }

    fun saveAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            val isNewAlimento = alimentoId == null || alimentoId == -1

            val caloriasCalculadas = calculateCalories(state.proteinas, state.carbos, state.grasas)

            val alimento = if (isNewAlimento) {
                Alimento(
                    id = 0,
                    nombre = state.nombre,
                    precio = parseDecimal(state.precio, allowNegative = false)?.toDouble(),
                    marca = state.marca.ifEmpty { null },
                    proteinas = parseMacroInput(state.proteinas),
                    carbos = parseMacroInput(state.carbos),
                    grasas = parseMacroInput(state.grasas),
                    cantidadBase = parseQuantity(state.cantidadBase),
                    unidadBase = state.unidadBase,
                    calorias = caloriasCalculadas,
                    detalles = state.detalles.ifEmpty { null }
                )
            } else {
                val now = System.currentTimeMillis()
                Alimento(
                    id = state.id,
                    nombre = state.nombre,
                    precio = parseDecimal(state.precio, allowNegative = false)?.toDouble(),
                    marca = state.marca.ifEmpty { null },
                    proteinas = parseMacroInput(state.proteinas),
                    carbos = parseMacroInput(state.carbos),
                    grasas = parseMacroInput(state.grasas),
                    cantidadBase = parseQuantity(state.cantidadBase),
                    unidadBase = state.unidadBase,
                    calorias = caloriasCalculadas,
                    fechaCreacion = state.fechaCreacion ?: now,
                    fechaActualizacion = now,
                    detalles = state.detalles.ifEmpty { null }
                )
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSaved = false) }
            try {
                if (isNewAlimento) {
                    Log.d(TAG, "Insertando nuevo alimento ${alimento.nombre}")
                    repository.insert(alimento)
                } else {
                    Log.d(TAG, "Actualizando alimento ${alimento.nombre} (id=${alimento.id})")
                    repository.update(alimento)
                }
                Log.i(TAG, "Alimento guardado correctamente: ${alimento.nombre}")
                _uiState.update {
                    it.copy(
                        isSaved = true,
                        isLoading = false,
                        errorMessage = null,
                        fechaCreacion = alimento.fechaCreacion,
                        fechaActualizacion = alimento.fechaActualizacion
                    )
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error al guardar alimento ${alimento.nombre}", ex)
                _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_saving_alimento) }
                _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_saving_alimento))
            }
        }
    }

    fun deleteAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.id != 0) {
                val cantidadBase = parseQuantity(state.cantidadBase)
                val alimentoToDelete = Alimento(
                    id = state.id,
                    nombre = state.nombre,
                    proteinas = parseMacroInput(state.proteinas),
                    carbos = parseMacroInput(state.carbos),
                    grasas = parseMacroInput(state.grasas),
                    cantidadBase = cantidadBase,
                    unidadBase = state.unidadBase,
                    calorias = calculateCalories(state.proteinas, state.carbos, state.grasas)
                )
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    Log.d(TAG, "Eliminando alimento ${alimentoToDelete.nombre} (id=${alimentoToDelete.id})")
                    repository.delete(alimentoToDelete)
                    Log.i(TAG, "Alimento eliminado correctamente: ${alimentoToDelete.nombre}")
                    _uiState.update { it.copy(isSaved = true, isLoading = false, errorMessage = null) }
                } catch (ex: Exception) {
                    Log.e(TAG, "Error al eliminar alimento ${alimentoToDelete.nombre} (id=${alimentoToDelete.id})", ex)
                    _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_deleting_alimento) }
                    _events.emit(AddEditAlimentoEvent.ShowMessage(R.string.error_deleting_alimento))
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

    private fun calculateCalories(proteinas: String, carbos: String, grasas: String): Double {
        val proteinasDouble = parseMacroInput(proteinas)
        val carbosDouble = parseMacroInput(carbos)
        val grasasDouble = parseMacroInput(grasas)
        return calculateCalories(proteinasDouble, carbosDouble, grasasDouble)
    }

    private fun calculateCalories(proteinas: Double, carbos: Double, grasas: Double): Double {
        return (proteinas * 4) + (carbos * 4) + (grasas * 9)
    }

    private fun parseMacroInput(value: String): Double {
        return parseDecimal(value)?.takeIf { it >= BigDecimal.ZERO }?.toDouble() ?: 0.0
    }

    private fun formatCalories(calorias: Double): String {
        return if (calorias == 0.0) {
            "0"
        } else {
            BigDecimal.valueOf(calorias)
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
        val parsed = parseDecimal(value, allowNegative = false)?.toDouble()
        return if (parsed != null && parsed > 0.0) parsed else 100.0
    }
    private fun parseDecimal(value: String, allowNegative: Boolean = true): BigDecimal? {
        val sanitized = sanitizeDecimalForParsing(value, allowNegative) ?: return null
        return runCatching { BigDecimal(sanitized) }.getOrNull()
    }

    private fun sanitizeDecimalForParsing(value: String, allowNegative: Boolean): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null

        val decimalSeparator = decimalSymbols.decimalSeparator
        val minusSign = decimalSymbols.minusSign
        val builder = StringBuilder(trimmed.length)

        var hasDecimal = false

        trimmed.forEachIndexed { index, char ->
            when {
                char.isDigit() -> builder.append(char)
                !hasDecimal && (char == '.' || char == ',' || char == decimalSeparator) -> {
                    builder.append('.')
                    hasDecimal = true
                }
                allowNegative && builder.isEmpty() && index == 0 && (char == '-' || char == minusSign) -> {
                    builder.append('-')
                }
                else -> Unit
            }
        }

        if (builder.isEmpty() || builder.toString() == "-" || builder.toString() == "." || builder.toString() == "-." ) {
            return null
        }

        return builder.toString()
    }
}
