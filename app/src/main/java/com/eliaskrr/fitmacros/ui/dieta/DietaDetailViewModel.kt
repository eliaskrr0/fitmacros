package com.eliaskrr.fitmacros.ui.dieta

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.data.repository.DietaAlimentoRepository
import com.eliaskrr.fitmacros.data.repository.UserDataRepository
import com.eliaskrr.fitmacros.domain.CalculationResult
import com.eliaskrr.fitmacros.domain.MacroCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MealData(
    val alimentos: List<Alimento> = emptyList(),
    val totalCalorias: Double = 0.0
)

class DietaDetailViewModel(
    savedStateHandle: SavedStateHandle,
    userDataRepository: UserDataRepository,
    private val dietaAlimentoRepository: DietaAlimentoRepository
) : ViewModel() {

    private val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])

    val nutrientGoals: StateFlow<CalculationResult> = userDataRepository.userData.map {
        MacroCalculator.calculate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculationResult()
    )

    fun getMealData(mealType: MealType): StateFlow<MealData> {
        return dietaAlimentoRepository.getAlimentosForDietaAndMeal(dietaId, mealType)
            .map { alimentos ->
                val totalCalorias = alimentos.sumOf { it.calorias }
                MealData(alimentos, totalCalorias)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MealData()
            )
    }

    fun addAlimentoToDieta(alimentoId: Int, mealType: MealType, cantidad: Double) {
        viewModelScope.launch {
            val dietaAlimento = DietaAlimento(dietaId, alimentoId, mealType, cantidad)
            dietaAlimentoRepository.insert(dietaAlimento)
        }
    }

    companion object {
        fun provideFactory(
            userDataRepository: UserDataRepository,
            dietaAlimentoRepository: DietaAlimentoRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return DietaDetailViewModel(
                    extras.createSavedStateHandle(),
                    userDataRepository,
                    dietaAlimentoRepository
                ) as T
            }
        }
    }
}
