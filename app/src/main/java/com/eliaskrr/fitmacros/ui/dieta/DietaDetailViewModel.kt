package com.eliaskrr.fitmacros.ui.dieta

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.DietaAlimentoWithAlimento
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.data.model.QuantityUnit
import com.eliaskrr.fitmacros.data.repository.DietaAlimentoRepository
import com.eliaskrr.fitmacros.data.repository.UserDataRepository
import com.eliaskrr.fitmacros.domain.MacroCalculator
import com.eliaskrr.fitmacros.domain.MacroCalculationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealData(
    val items: List<MealItem> = emptyList(),
    val totalCalorias: Double = 0.0,
    val totalProteinas: Double = 0.0,
    val totalCarbos: Double = 0.0,
    val totalGrasas: Double = 0.0
)

data class MealItem(
    val alimento: com.eliaskrr.fitmacros.data.model.Alimento,
    val cantidad: Double,
    val unidad: QuantityUnit,
    val calorias: Double,
    val proteinas: Double,
    val carbos: Double,
    val grasas: Double
)

data class DietaDetailUiState(
    val isSelectionMode: Boolean = false,
    val selectedItems: Map<MealType, Set<Int>> = emptyMap(),
    val editQuantityState: EditQuantityState? = null
) {
    val selectedCount: Int
        get() = selectedItems.values.sumOf { it.size }
}

data class EditQuantityState(
    val mealType: MealType,
    val alimentoId: Int,
    val nombre: String,
    val unidad: QuantityUnit,
    val quantityText: String,
    val showError: Boolean = false
)

@HiltViewModel
class DietaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userDataRepository: UserDataRepository,
    private val dietaAlimentoRepository: DietaAlimentoRepository
) : ViewModel() {

    private val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])

    private val _uiState = MutableStateFlow(DietaDetailUiState())
    val uiState: StateFlow<DietaDetailUiState> = _uiState.asStateFlow()

    val nutrientGoals: StateFlow<MacroCalculationResult> = userDataRepository.userData.map {
        MacroCalculator.calculate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MacroCalculationResult.Idle
    )

    private val mealDataFlows: MutableMap<MealType, StateFlow<MealData>> = mutableMapOf()

    fun getMealData(mealType: MealType): StateFlow<MealData> {
        return mealDataFlows.getOrPut(mealType) {
            Log.d(TAG, "Obteniendo datos de comida $mealType para dieta $dietaId")
            dietaAlimentoRepository.getAlimentosForDietaAndMeal(dietaId, mealType)
                .map { registros ->
                    val items = registros.map { it.toMealItem() }
                    val totalCalorias = items.sumOf { it.calorias }
                    val totalProteinas = items.sumOf { it.proteinas }
                    val totalCarbos = items.sumOf { it.carbos }
                    val totalGrasas = items.sumOf { it.grasas }
                    MealData(
                        items = items,
                        totalCalorias = totalCalorias,
                        totalProteinas = totalProteinas,
                        totalCarbos = totalCarbos,
                        totalGrasas = totalGrasas
                    )
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = MealData()
                )
        }
    }

    fun addAlimentoToDieta(alimentoId: Int, mealType: MealType, cantidad: Double, unidad: QuantityUnit) {
        viewModelScope.launch {
            val dietaAlimento = DietaAlimento(dietaId, alimentoId, mealType, cantidad, unidad)
            runCatching {
                Log.d(
                    TAG,
                    "Añadiendo alimento $alimentoId a dieta $dietaId para $mealType con cantidad $cantidad $unidad"
                )
                dietaAlimentoRepository.insert(dietaAlimento)
            }.onSuccess {
                Log.i(TAG, "Alimento $alimentoId añadido a dieta $dietaId para $mealType")
            }.onFailure { ex ->
                Log.e(TAG, "Error al añadir alimento $alimentoId a dieta $dietaId", ex)
            }
        }
    }

    fun toggleSelection(mealType: MealType, alimentoId: Int) {
        _uiState.update { state ->
            val currentSelection = state.selectedItems[mealType].orEmpty().toMutableSet()
            if (!currentSelection.add(alimentoId)) {
                currentSelection.remove(alimentoId)
            }
            val newSelection = state.selectedItems.toMutableMap().apply {
                if (currentSelection.isEmpty()) {
                    remove(mealType)
                } else {
                    put(mealType, currentSelection)
                }
            }
            state.copy(
                isSelectionMode = newSelection.isNotEmpty(),
                selectedItems = newSelection
            )
        }
    }

    fun clearSelection() {
        _uiState.value = DietaDetailUiState()
    }

    fun deleteSelected() {
        val currentState = _uiState.value
        val selections = currentState.selectedItems
        if (selections.isEmpty()) return

        viewModelScope.launch {
            runCatching {
                selections.forEach { (mealType, alimentoIds) ->
                    alimentoIds.forEach { alimentoId ->
                        dietaAlimentoRepository.delete(dietaId, alimentoId, mealType)
                    }
                }
            }.onSuccess {
                Log.i(TAG, "Eliminados ${currentState.selectedCount} alimentos seleccionados de la dieta $dietaId")
                clearSelection()
            }.onFailure { ex ->
                Log.e(TAG, "Error al eliminar alimentos seleccionados de la dieta $dietaId", ex)
            }
        }
    }

    fun startEditQuantity(mealType: MealType, item: MealItem) {
        _uiState.update { state ->
            state.copy(
                editQuantityState = EditQuantityState(
                    mealType = mealType,
                    alimentoId = item.alimento.id,
                    nombre = item.alimento.nombre,
                    unidad = item.unidad,
                    quantityText = formatQuantity(item.cantidad)
                )
            )
        }
    }

    fun updateEditQuantity(value: String) {
        _uiState.update { state ->
            val editState = state.editQuantityState ?: return@update state
            state.copy(editQuantityState = editState.copy(quantityText = value, showError = false))
        }
    }

    fun dismissEditQuantity() {
        _uiState.update { state -> state.copy(editQuantityState = null) }
    }

    fun confirmEditQuantity() {
        val editState = _uiState.value.editQuantityState ?: return
        val normalized = editState.quantityText.replace(',', '.').toDoubleOrNull()
        if (normalized == null || normalized <= 0.0) {
            _uiState.update { state ->
                state.copy(editQuantityState = editState.copy(showError = true))
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                dietaAlimentoRepository.updateCantidad(
                    dietaId = dietaId,
                    alimentoId = editState.alimentoId,
                    mealType = editState.mealType,
                    cantidad = normalized
                )
            }.onSuccess {
                Log.i(
                    TAG,
                    "Cantidad actualizada para alimento ${editState.alimentoId} en dieta $dietaId (${editState.mealType}) a $normalized"
                )
                dismissEditQuantity()
            }.onFailure { ex ->
                Log.e(
                    TAG,
                    "Error al actualizar cantidad del alimento ${editState.alimentoId} en dieta $dietaId",
                    ex
                )
                _uiState.update { state ->
                    state.copy(editQuantityState = editState.copy(showError = true))
                }
            }
        }
    }

    companion object {
        private const val TAG = "DietaDetailVM"
    }
}

private fun DietaAlimentoWithAlimento.toMealItem(): MealItem {
    val baseQuantity = alimento.cantidadBase.takeIf { it > 0.0 } ?: 100.0
    val factor = cantidad / baseQuantity
    return MealItem(
        alimento = alimento,
        cantidad = cantidad,
        unidad = unidad,
        calorias = alimento.calorias * factor,
        proteinas = alimento.proteinas * factor,
        carbos = alimento.carbos * factor,
        grasas = alimento.grasas * factor
    )
}

private fun formatQuantity(value: Double): String {
    return java.math.BigDecimal.valueOf(value)
        .setScale(2, java.math.RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}
