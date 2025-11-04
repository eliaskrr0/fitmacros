package com.eliaskrr.fitmacros.ui.alimento

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class AlimentoUiState(
    val id: Int = 0,
    val nombre: String = "",
    val precio: String = "",
    val marca: String = "",
    val proteinas: String = "",
    val carbos: String = "",
    val grasas: String = "",
    val calorias: String = "0",
    val detalles: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class AddEditAlimentoViewModel @Inject constructor(
    private val repository: AlimentoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlimentoUiState())
    val uiState: StateFlow<AlimentoUiState> = _uiState.asStateFlow()

    private val alimentoId: Int? = savedStateHandle["alimentoId"]

    init {
        // El valor por defecto de alimentoId es -1, así que solo cargamos si es un ID válido
        if (alimentoId != null && alimentoId != -1) {
            Log.d(TAG, "Inicializando edición para alimento con id $alimentoId")
            loadAlimento(alimentoId)
        }
    }

    private fun loadAlimento(id: Int) {
        viewModelScope.launch {
            runCatching { repository.getById(id).first() }
                .onSuccess { alimento ->
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
                                calorias = formatCalories(caloriasCalculadas),
                                detalles = alimento.detalles ?: ""
                            )
                        }
                    } else {
                        Log.w(TAG, "No se encontró el alimento con id $id")
                    }
                }
                .onFailure { ex ->
                    Log.e(TAG, "Error cargando alimento con id $id", ex)
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
        detalles: String? = null
    ) {
        _uiState.update { currentState ->
            val updatedProteinas = proteinas ?: currentState.proteinas
            val updatedCarbos = carbos ?: currentState.carbos
            val updatedGrasas = grasas ?: currentState.grasas

            val calculatedCalories = calculateCalories(updatedProteinas, updatedCarbos, updatedGrasas)

            currentState.copy(
                nombre = nombre ?: currentState.nombre,
                precio = precio ?: currentState.precio,
                marca = marca ?: currentState.marca,
                proteinas = updatedProteinas,
                carbos = updatedCarbos,
                grasas = updatedGrasas,
                calorias = formatCalories(calculatedCalories),
                detalles = detalles ?: currentState.detalles
            )
        }
    }

    fun saveAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            val isNewAlimento = alimentoId == null || alimentoId == -1

            val caloriasCalculadas = calculateCalories(state.proteinas, state.carbos, state.grasas)

            val alimento = Alimento(
                id = if (isNewAlimento) 0 else state.id,
                nombre = state.nombre,
                precio = state.precio.toDoubleOrNull(),
                marca = state.marca.ifEmpty { null },
                proteinas = state.proteinas.toDoubleOrNull() ?: 0.0,
                carbos = state.carbos.toDoubleOrNull() ?: 0.0,
                grasas = state.grasas.toDoubleOrNull() ?: 0.0,
                calorias = caloriasCalculadas,
                detalles = state.detalles.ifEmpty { null }
            )

            runCatching {
                if (isNewAlimento) {
                    Log.d(TAG, "Insertando nuevo alimento ${alimento.nombre}")
                    repository.insert(alimento)
                } else {
                    Log.d(TAG, "Actualizando alimento ${alimento.nombre} (id=${alimento.id})")
                    repository.update(alimento)
                }
            }.onSuccess {
                Log.i(TAG, "Alimento guardado correctamente: ${alimento.nombre}")
                _uiState.update { it.copy(isSaved = true) }
            }.onFailure { ex ->
                Log.e(TAG, "Error al guardar alimento ${alimento.nombre}", ex)
            }
        }
    }

    fun deleteAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.id != 0) {
                val alimentoToDelete = Alimento(
                    id = state.id,
                    nombre = state.nombre,
                    proteinas = state.proteinas.toDoubleOrNull() ?: 0.0,
                    carbos = state.carbos.toDoubleOrNull() ?: 0.0,
                    grasas = state.grasas.toDoubleOrNull() ?: 0.0,
                    calorias = calculateCalories(state.proteinas, state.carbos, state.grasas)
                )
                runCatching {
                    Log.d(TAG, "Eliminando alimento ${alimentoToDelete.nombre} (id=${alimentoToDelete.id})")
                    repository.delete(alimentoToDelete)
                }.onSuccess {
                    Log.i(TAG, "Alimento eliminado correctamente: ${alimentoToDelete.nombre}")
                    _uiState.update { it.copy(isSaved = true) }
                }.onFailure { ex ->
                    Log.e(TAG, "Error al eliminar alimento ${alimentoToDelete.nombre} (id=${alimentoToDelete.id})", ex)
                }
            }
        }
    }

    companion object {
        private const val TAG = "AddEditAlimentoVM"
    }

    private fun calculateCalories(proteinas: String, carbos: String, grasas: String): Double {
        val proteinasDouble = proteinas.toDoubleOrNull() ?: 0.0
        val carbosDouble = carbos.toDoubleOrNull() ?: 0.0
        val grasasDouble = grasas.toDoubleOrNull() ?: 0.0
        return calculateCalories(proteinasDouble, carbosDouble, grasasDouble)
    }

    private fun calculateCalories(proteinas: Double, carbos: Double, grasas: Double): Double {
        return (proteinas * 4) + (carbos * 4) + (grasas * 9)
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
}
