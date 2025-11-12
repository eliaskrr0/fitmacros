package com.eliaskrr.fitmacros.ui.diet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.entity.nutrition.DietFood
import com.eliaskrr.fitmacros.data.entity.nutrition.Meal
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import com.eliaskrr.fitmacros.data.repository.nutrition.DietFoodRepository
import com.eliaskrr.fitmacros.data.repository.user.UserDataRepository
import com.eliaskrr.fitmacros.domain.MacroCalculator
import com.eliaskrr.fitmacros.domain.MacroCalculationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealData(
    val items: List<MealItem> = emptyList(),
    val totalCalories: Double = 0.0,
    val totalProteins: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFats: Double = 0.0
)

data class MealItem(
    val food: Food,
    val amount: Double,
    val unit: QuantityUnit,
    val calories: Double,
    val proteins: Double,
    val carbs: Double,
    val fats: Double
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
    val foodId: Int,
    val name: String,
    val unit: QuantityUnit,
    val quantityText: String,
    val showError: Boolean = false
)

@HiltViewModel
class DietaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userDataRepository: UserDataRepository,
    private val dietFoodRepository: DietFoodRepository
) : ViewModel() {

    private val dietId: Int = checkNotNull(savedStateHandle["dietId"])

    private val _uiState = MutableStateFlow(DietaDetailUiState())
    val uiState: StateFlow<DietaDetailUiState> = _uiState.asStateFlow()

    val nutrientGoals: StateFlow<MacroCalculationResult> = userDataRepository.userData
        .distinctUntilChanged()
        .map {
        MacroCalculator.calculate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MacroCalculationResult.Idle
    )

    private val mealDataFlows: MutableMap<MealType, StateFlow<MealData>> = mutableMapOf()

    fun getMealData(mealType: MealType): StateFlow<MealData> {
        return mealDataFlows.getOrPut(mealType) {
            Log.d(TAG, "Obteniendo datos de comida $mealType para dieta $dietId")
            dietFoodRepository.getAlimentosForDietaAndMeal(dietId, mealType)
                .map { registros ->
                    val items = registros.map { it.toMealItem() }
                    val totalCalories = items.sumOf { it.calories }
                    val totalProteins = items.sumOf { it.proteins }
                    val totalCarbs = items.sumOf { it.carbs }
                    val totalFats = items.sumOf { it.fats }
                    MealData(
                        items = items,
                        totalCalories = totalCalories,
                        totalProteins = totalProteins,
                        totalCarbs = totalCarbs,
                        totalFats = totalFats
                    )
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = MealData()
                )
        }
    }

    fun addAlimentoToDieta(foodId: Int, mealType: MealType, amount: Double, unit: QuantityUnit) {
        viewModelScope.launch {
            val dietFood = DietFood(dietId, foodId, mealType, amount, unit)
            runCatching {
                Log.d(
                    TAG,
                    "Añadiendo alimento $foodId a dieta $dietId para $mealType con cantidad $amount $unit"
                )
                dietFoodRepository.insert(dietFood)
            }.onSuccess {
                Log.i(TAG, "Alimento $foodId añadido a dieta $dietId para $mealType")
            }.onFailure { ex ->
                Log.e(TAG, "Error al añadir alimento $foodId a dieta $dietId", ex)
            }
        }
    }

    fun toggleSelection(mealType: MealType, foodId: Int) {
        _uiState.update { state ->
            val currentSelection = state.selectedItems[mealType].orEmpty().toMutableSet()
            if (!currentSelection.add(foodId)) {
                currentSelection.remove(foodId)
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
                selections.forEach { (mealType, foodIds) ->
                    foodIds.forEach { foodId ->
                        dietFoodRepository.delete(dietId, foodId, mealType)
                    }
                }
            }.onSuccess {
                Log.i(TAG, "Eliminados ${currentState.selectedCount} alimentos seleccionados de la dieta $dietId")
                clearSelection()
            }.onFailure { ex ->
                Log.e(TAG, "Error al eliminar alimentos seleccionados de la dieta $dietId", ex)
            }
        }
    }

    fun startEditQuantity(mealType: MealType, item: MealItem) {
        _uiState.update { state ->
            state.copy(
                editQuantityState = EditQuantityState(
                    mealType = mealType,
                    foodId = item.food.id,
                    name = item.food.name,
                    unit = item.unit,
                    quantityText = formatQuantity(item.amount)
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
                dietFoodRepository.updateCantidad(
                    dietId = dietId,
                    foodId = editState.foodId,
                    mealType = editState.mealType,
                    amount = normalized
                )
            }.onSuccess {
                Log.i(
                    TAG,
                    "Cantidad actualizada para alimento ${editState.foodId} en dieta $dietId (${editState.mealType}) a $normalized"
                )
                dismissEditQuantity()
            }.onFailure { ex ->
                Log.e(
                    TAG,
                    "Error al actualizar cantidad del alimento ${editState.foodId} en dieta $dietId",
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

private fun Meal.toMealItem(): MealItem {
    val baseQuantity = food.amountBase.takeIf { it > 0.0 } ?: 100.0
    val factor = amount / baseQuantity
    return MealItem(
        food = food,
        amount = amount,
        unit = unit,
        calories = food.calories * factor,
        proteins = food.proteins * factor,
        carbs = food.carbs * factor,
        fats = food.fats * factor
    )
}

private fun formatQuantity(value: Double): String {
    return java.math.BigDecimal.valueOf(value)
        .setScale(2, java.math.RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}
