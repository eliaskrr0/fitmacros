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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

@HiltViewModel
class DietaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userDataRepository: UserDataRepository,
    private val dietaAlimentoRepository: DietaAlimentoRepository
) : ViewModel() {

    private val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])

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
