package com.eliaskrr.fitmacros.ui.food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Food
import com.eliaskrr.fitmacros.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlimentoViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(AlimentosUiState(isLoading = true))
    val uiState: StateFlow<AlimentosUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AlimentoEvent>()
    val events: SharedFlow<AlimentoEvent> = _events.asSharedFlow()

    init {
        observeAlimentos()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(errorMessage = null, selectedAlimentos = emptySet()) }
    }

    fun insert(food: Food) = viewModelScope.launch {
        try {
            Log.d(TAG, "Solicitando inserción de alimento ${food.nombre}")
            repository.insert(food)
        } catch (ex: Exception) {
            Log.e(TAG, "Error solicitando inserción de ${food.nombre}", ex)
            notifyError(R.string.error_saving_alimento)
        }
    }

    fun update(food: Food) = viewModelScope.launch {
        try {
            Log.d(TAG, "Solicitando actualización de alimento ${food.nombre} (id=${food.id})")
            repository.update(food)
        } catch (ex: Exception) {
            Log.e(TAG, "Error solicitando actualización de ${food.nombre} (id=${food.id})", ex)
            notifyError(R.string.error_saving_alimento)
        }
    }

    fun delete(food: Food) = viewModelScope.launch {
        try {
            Log.d(TAG, "Solicitando eliminación de alimento ${food.nombre} (id=${food.id})")
            repository.delete(food)
        } catch (ex: Exception) {
            Log.e(TAG, "Error solicitando eliminación de ${food.nombre} (id=${food.id})", ex)
            notifyError(R.string.error_deleting_alimento)
        }
    }

    fun toggleSelection(alimentoId: Int) {
        _uiState.update { state ->
            val updatedSelection = state.selectedAlimentos.toMutableSet().also { selection ->
                if (!selection.add(alimentoId)) {
                    selection.remove(alimentoId)
                }
            }
            state.copy(selectedAlimentos = updatedSelection)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedAlimentos = emptySet()) }
    }

    fun deleteSelected() = viewModelScope.launch {
        val selectedIds = _uiState.value.selectedAlimentos
        if (selectedIds.isEmpty()) return@launch

        val alimentosToDelete = _uiState.value.foods.filter { it.id in selectedIds }
        var hadError = false

        alimentosToDelete.forEach { alimento ->
            try {
                Log.d(TAG, "Eliminando alimento seleccionado ${alimento.nombre} (id=${alimento.id})")
                repository.delete(alimento)
            } catch (ex: Exception) {
                hadError = true
                Log.e(TAG, "Error eliminando alimento seleccionado ${alimento.nombre} (id=${alimento.id})", ex)
            }
        }

        _uiState.update {
            it.copy(
                selectedAlimentos = emptySet(),
                errorMessage = if (hadError) R.string.error_deleting_alimento else null
            )
        }

        if (hadError) {
            _events.emit(AlimentoEvent.ShowMessage(R.string.error_deleting_alimento))
        } else if (alimentosToDelete.isNotEmpty()) {
            _events.emit(AlimentoEvent.ShowMessage(R.string.alimentos_deleted_message))
        }
    }

    private fun observeAlimentos() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val alimentosFlow = if (query.isBlank()) {
                    Log.d(TAG, "Buscando todos los alimentos")
                    repository.getAllAlimentos()
                } else {
                    Log.d(TAG, "Buscando alimentos por nombre: $query")
                    repository.getAlimentosByName(query)
                }

                alimentosFlow
                    .catch { ex ->
                        Log.e(TAG, "Error cargando alimentos para la búsqueda '$query'", ex)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                foods = emptyList(),
                                errorMessage = R.string.error_loading_alimentos
                            )
                        }
                        _events.emit(AlimentoEvent.ShowMessage(R.string.error_loading_alimentos))
                    }
                    .collect { alimentos ->
                        _uiState.update { state ->
                            val filteredSelection = state.selectedAlimentos.filter { id ->
                                alimentos.any { alimento -> alimento.id == id }
                            }.toSet()
                            state.copy(
                                foods = alimentos,
                                isLoading = false,
                                errorMessage = null,
                                selectedAlimentos = filteredSelection
                            )
                        }
                    }
            }
        }
    }

    private suspend fun notifyError(messageRes: Int) {
        _uiState.update { it.copy(errorMessage = messageRes) }
        _events.emit(AlimentoEvent.ShowMessage(messageRes))
    }

    companion object {
        private const val TAG = "AlimentoViewModel"
    }

    data class AlimentosUiState(
        val foods: List<Food> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: Int? = null,
        val selectedAlimentos: Set<Int> = emptySet()
    ) {
        val isSelectionMode: Boolean
            get() = selectedAlimentos.isNotEmpty()
    }

    sealed interface AlimentoEvent {
        data class ShowMessage(val messageRes: Int) : AlimentoEvent
    }
}
