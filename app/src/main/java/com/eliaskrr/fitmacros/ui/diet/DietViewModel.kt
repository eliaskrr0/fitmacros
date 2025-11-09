package com.eliaskrr.fitmacros.ui.diet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.entity.nutrition.Diet
import com.eliaskrr.fitmacros.data.entity.nutrition.Meal
import com.eliaskrr.fitmacros.data.repository.nutrition.DietFoodRepository
import com.eliaskrr.fitmacros.data.repository.nutrition.DietRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRepository: DietRepository,
    private val dietFoodRepository: DietFoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietasUiState(isLoading = true))
    val uiState: StateFlow<DietasUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DietaEvent>()
    val events: SharedFlow<DietaEvent> = _events.asSharedFlow()

    init {
        observeDietas()
    }

    suspend fun getAlimentosOfDieta(dietId: Int): List<Meal> {
        return dietFoodRepository.getAlimentosForDieta(dietId).first()
    }

    fun insert(diet: Diet) = viewModelScope.launch {
        runCatching {
            Log.d(TAG, "Solicitando inserción de dieta ${diet.name}")
            dietRepository.insert(diet)
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando inserción de dieta ${diet.name}", ex)
        }
    }

    fun toggleSelection(dietId: Int) {
        _uiState.update { state ->
            val updatedSelection = state.selectedDietas.toMutableSet().also { selection ->
                if (!selection.add(dietId)) {
                    selection.remove(dietId)
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
            dietRepository.deleteDietas(selectedIds)
        }.onSuccess {
            _uiState.update { it.copy(selectedDietas = emptySet(), errorMessage = null) }
            _events.emit(DietaEvent.ShowMessage(R.string.dietas_deleted_message))
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando eliminación de dietas: ${selectedIds.joinToString()}", ex)
            _uiState.update { it.copy(errorMessage = R.string.error_deleting_diet) }
            _events.emit(DietaEvent.ShowMessage(R.string.error_deleting_diet))
        }
    }

    private fun observeDietas() {
        viewModelScope.launch {
            dietRepository.allDietas
                .catch { ex ->
                    Log.e(TAG, "Error cargando dietas", ex)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            diets = emptyList(),
                            errorMessage = R.string.error_loading_diets
                        )
                    }
                    _events.emit(DietaEvent.ShowMessage(R.string.error_loading_diets))
                }
                .collect { dietas ->
                    _uiState.update { state ->
                        val filteredSelection = state.selectedDietas.filter { id ->
                            dietas.any { it.id == id }
                        }.toSet()
                        state.copy(
                            diets = dietas,
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
        val diets: List<Diet> = emptyList(),
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
