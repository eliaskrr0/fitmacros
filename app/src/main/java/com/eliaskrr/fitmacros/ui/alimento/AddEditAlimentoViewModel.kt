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
import javax.inject.Inject

data class AlimentoUiState(
    val id: Int = 0,
    val nombre: String = "",
    val precio: String = "",
    val marca: String = "",
    val proteinas: String = "",
    val carbos: String = "",
    val grasas: String = "",
    val calorias: String = "",
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
                        _uiState.update {
                            it.copy(
                                id = alimento.id,
                                nombre = alimento.nombre,
                                precio = alimento.precio?.toString() ?: "",
                                marca = alimento.marca ?: "",
                                proteinas = alimento.proteinas.toString(),
                                carbos = alimento.carbos.toString(),
                                grasas = alimento.grasas.toString(),
                                calorias = alimento.calorias.toString(),
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

    fun onValueChange(nombre: String? = null, precio: String? = null, marca: String? = null, proteinas: String? = null, carbos: String? = null, grasas: String? = null, calorias: String? = null, detalles: String? = null) {
        _uiState.update {
            it.copy(
                nombre = nombre ?: it.nombre,
                precio = precio ?: it.precio,
                marca = marca ?: it.marca,
                proteinas = proteinas ?: it.proteinas,
                carbos = carbos ?: it.carbos,
                grasas = grasas ?: it.grasas,
                calorias = calorias ?: it.calorias,
                detalles = detalles ?: it.detalles
            )
        }
    }

    fun saveAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            val isNewAlimento = alimentoId == null || alimentoId == -1

            val alimento = Alimento(
                id = if (isNewAlimento) 0 else state.id,
                nombre = state.nombre,
                precio = state.precio.toDoubleOrNull(),
                marca = state.marca.ifEmpty { null },
                proteinas = state.proteinas.toDoubleOrNull() ?: 0.0,
                carbos = state.carbos.toDoubleOrNull() ?: 0.0,
                grasas = state.grasas.toDoubleOrNull() ?: 0.0,
                calorias = state.calorias.toDoubleOrNull() ?: 0.0,
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
                val alimentoToDelete = Alimento(id = state.id, nombre = state.nombre, proteinas = state.proteinas.toDoubleOrNull() ?: 0.0, carbos = state.carbos.toDoubleOrNull() ?: 0.0, grasas = state.grasas.toDoubleOrNull() ?: 0.0, calorias = state.calorias.toDoubleOrNull() ?: 0.0)
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
}
