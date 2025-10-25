package com.eliaskrr.fitmacros.ui.alimento

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class AddEditAlimentoViewModel(
    private val repository: AlimentoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlimentoUiState())
    val uiState: StateFlow<AlimentoUiState> = _uiState.asStateFlow()

    private val alimentoId: Int? = savedStateHandle["alimentoId"]

    init {
        // El valor por defecto de alimentoId es -1, así que solo cargamos si es un ID válido
        if (alimentoId != null && alimentoId != -1) {
            loadAlimento(alimentoId)
        }
    }

    private fun loadAlimento(id: Int) {
        viewModelScope.launch {
            repository.getById(id).first { alimento ->
                if (alimento != null) {
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
                }
                true
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

            if (isNewAlimento) {
                repository.insert(alimento)
            } else {
                repository.update(alimento)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
    
    fun deleteAlimento() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.id != 0) {
                val alimentoToDelete = Alimento(id = state.id, nombre = state.nombre, proteinas = state.proteinas.toDoubleOrNull() ?: 0.0, carbos = state.carbos.toDoubleOrNull() ?: 0.0, grasas = state.grasas.toDoubleOrNull() ?: 0.0, calorias = state.calorias.toDoubleOrNull() ?: 0.0)
                repository.delete(alimentoToDelete)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: AlimentoRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return AddEditAlimentoViewModel(repository, savedStateHandle) as T
            }
        }
    }
}
