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
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import com.eliaskrr.fitmacros.data.repository.DietaAlimentoRepository
import com.eliaskrr.fitmacros.data.repository.UserDataRepository
import com.eliaskrr.fitmacros.domain.CalculationResult
import com.eliaskrr.fitmacros.domain.MacroCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NutrientTotals(
    val calories: Double = 0.0,
    val proteins: Double = 0.0,
    val carbs: Double = 0.0,
    val fats: Double = 0.0
)

data class DietaMealEntryUi(
    val entryId: Int,
    val alimentoId: Int,
    val mealType: MealType,
    val alimentoName: String,
    val servings: Double,
    val calories: Double,
    val proteins: Double,
    val carbs: Double,
    val fats: Double
)

data class DietaDetailUiState(
    val meals: Map<MealType, List<DietaMealEntryUi>> = MealType.values().associateWith { emptyList<DietaMealEntryUi>() },
    val totals: NutrientTotals = NutrientTotals(),
    val availableFoods: List<Alimento> = emptyList()
)

class DietaDetailViewModel(
    savedStateHandle: SavedStateHandle,
    userDataRepository: UserDataRepository,
    private val dietaAlimentoRepository: DietaAlimentoRepository,
    private val alimentoRepository: AlimentoRepository
) : ViewModel() {

    private val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])

    val nutrientGoals: StateFlow<CalculationResult> = userDataRepository.userData.map {
        MacroCalculator.calculate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculationResult()
    )

    private val dietaAlimentos = dietaAlimentoRepository.getByDieta(dietaId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val availableFoods = alimentoRepository.getAllAlimentos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<DietaDetailUiState> = combine(dietaAlimentos, availableFoods) { entries, alimentos ->
        val meals = MealType.values().associateWith { mutableListOf<DietaMealEntryUi>() }
        var totalCalories = 0.0
        var totalProteins = 0.0
        var totalCarbs = 0.0
        var totalFats = 0.0

        entries.forEach { entry ->
            val alimento = entry.alimento
            val servings = entry.dietaAlimento.servings
            val calories = alimento.calorias * servings
            val proteins = alimento.proteinas * servings
            val carbs = alimento.carbos * servings
            val fats = alimento.grasas * servings

            totalCalories += calories
            totalProteins += proteins
            totalCarbs += carbs
            totalFats += fats

            meals[entry.dietaAlimento.mealType]?.add(
                DietaMealEntryUi(
                    entryId = entry.dietaAlimento.id,
                    alimentoId = alimento.id,
                    mealType = entry.dietaAlimento.mealType,
                    alimentoName = alimento.nombre,
                    servings = servings,
                    calories = calories,
                    proteins = proteins,
                    carbs = carbs,
                    fats = fats
                )
            )
        }

        DietaDetailUiState(
            meals = meals.mapValues { (_, value) -> value.sortedBy { it.alimentoName } },
            totals = NutrientTotals(
                calories = totalCalories,
                proteins = totalProteins,
                carbs = totalCarbs,
                fats = totalFats
            ),
            availableFoods = alimentos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DietaDetailUiState()
    )

    fun addFoodToMeal(alimentoId: Int, mealType: MealType, servings: Double) {
        if (servings <= 0) return
        viewModelScope.launch {
            dietaAlimentoRepository.insert(
                DietaAlimento(
                    dietaId = dietaId,
                    alimentoId = alimentoId,
                    mealType = mealType,
                    servings = servings
                )
            )
        }
    }

    fun removeFood(entryId: Int) {
        viewModelScope.launch {
            dietaAlimentoRepository.deleteById(entryId)
        }
    }

    companion object {
        fun provideFactory(
            userDataRepository: UserDataRepository,
            dietaAlimentoRepository: DietaAlimentoRepository,
            alimentoRepository: AlimentoRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return DietaDetailViewModel(
                    extras.createSavedStateHandle(),
                    userDataRepository,
                    dietaAlimentoRepository,
                    alimentoRepository
                ) as T
            }
        }
    }
}
