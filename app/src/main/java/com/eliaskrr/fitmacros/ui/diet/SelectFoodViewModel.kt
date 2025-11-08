package com.eliaskrr.fitmacros.ui.diet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import com.eliaskrr.fitmacros.data.entity.nutrition.DietFood
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import com.eliaskrr.fitmacros.data.repository.nutrition.FoodRepository
import com.eliaskrr.fitmacros.data.repository.nutrition.DietFoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SelectFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val dietFoodRepository: DietFoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])
    val mealType: MealType = MealType.valueOf(checkNotNull(savedStateHandle["mealType"]))

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val alimentos: StateFlow<List<Food>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                Log.d(TAG, "Cargando todos los alimentos para selección")
                foodRepository.getAllAlimentos()
            } else {
                Log.d(TAG, "Buscando alimentos para selección: $query")
                foodRepository.getAlimentosByName(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addAlimentoToDieta(alimentoId: Int, cantidad: Double) {
        viewModelScope.launch {
            val dietFood = DietFood(
                dietaId = dietaId,
                alimentoId = alimentoId,
                mealType = mealType,
                cantidad = cantidad
            )
            dietFoodRepository.insert(dietFood)
            _isSaved.value = true // Para disparar la navegación hacia atrás
        }
    }

    companion object {
        private const val TAG = "SelectAlimentoVM"
    }
}
