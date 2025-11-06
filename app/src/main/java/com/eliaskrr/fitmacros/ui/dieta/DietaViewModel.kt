package com.eliaskrr.fitmacros.ui.dieta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Dieta
import com.eliaskrr.fitmacros.data.repository.DietaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietaViewModel @Inject constructor(
    private val repository: DietaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietasUiState(isLoading = true))
    val uiState: StateFlow<DietasUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DietaEvent>()
    val events: SharedFlow<DietaEvent> = _events.asSharedFlow()

    init {
        observeDietas()
    }

    fun insert(dieta: Dieta) = viewModelScope.launch {
        runCatching {
            Log.d(TAG, "Solicitando inserción de dieta ${dieta.nombre}")
            repository.insert(dieta)
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando inserción de dieta ${dieta.nombre}", ex)
        }
    }

    fun toggleSelection(dietaId: Int) {
        _uiState.update { state ->
            val updatedSelection = state.selectedDietas.toMutableSet().also { selection ->
                if (!selection.add(dietaId)) {
                    selection.remove(dietaId)
                }
            }
            state.copy(selectedDietas = updatedSelection)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedDietas = emptySet()) }
    }

    fun deleteSelected() = viewModelScope.launch {
        val selectedIds = _uiState.value.selectedDietas
        if (selectedIds.isEmpty()) return@launch

        runCatching {
            Log.d(TAG, "Solicitando eliminación de dietas: ${selectedIds.joinToString()}")
            repository.deleteDietas(selectedIds)
        }.onSuccess {
            _uiState.update { it.copy(selectedDietas = emptySet(), errorMessage = null) }
            _events.emit(DietaEvent.ShowMessage(R.string.dietas_deleted_message))
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando eliminación de dietas: ${selectedIds.joinToString()}", ex)
            _uiState.update { it.copy(errorMessage = R.string.error_deleting_dieta) }
            _events.emit(DietaEvent.ShowMessage(R.string.error_deleting_dieta))
        }
    }

    private fun observeDietas() {
        viewModelScope.launch {
            repository.allDietas
                .catch { ex ->
                    Log.e(TAG, "Error cargando dietas", ex)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dietas = emptyList(),
                            errorMessage = R.string.error_loading_dietas
                        )
                    }
                    _events.emit(DietaEvent.ShowMessage(R.string.error_loading_dietas))
                }
                .collect { dietas ->
                    _uiState.update { state ->
                        val filteredSelection = state.selectedDietas.filter { id ->
                            dietas.any { it.id == id }
                        }.toSet()
                        state.copy(
                            dietas = dietas,
                            isLoading = false,
                            errorMessage = null,
                            selectedDietas = filteredSelection
                        )
                    }
                }
        }
    }

    companion object {
        private const val TAG = "DietaViewModel"
    }

    data class DietasUiState(
        val dietas: List<Dieta> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: Int? = null,
        val selectedDietas: Set<Int> = emptySet()
    ) {
        val isSelectionMode: Boolean
            get() = selectedDietas.isNotEmpty()
    }

    sealed interface DietaEvent {
        data class ShowMessage(val messageRes: Int) : DietaEvent
    }
}
