package com.eliaskrr.fitmacros.ui.dieta

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import com.eliaskrr.fitmacros.data.repository.DietaAlimentoRepository
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
class SelectAlimentoViewModel @Inject constructor(
    private val alimentoRepository: AlimentoRepository,
    private val dietaAlimentoRepository: DietaAlimentoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])
    val mealType: MealType = MealType.valueOf(checkNotNull(savedStateHandle["mealType"]))

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val alimentos: StateFlow<List<Alimento>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                Log.d(TAG, "Cargando todos los alimentos para selección")
                alimentoRepository.getAllAlimentos()
            } else {
                Log.d(TAG, "Buscando alimentos para selección: $query")
                alimentoRepository.getAlimentosByName(query)
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
            val dietaAlimento = DietaAlimento(
                dietaId = dietaId,
                alimentoId = alimentoId,
                mealType = mealType,
                cantidad = cantidad
            )
            dietaAlimentoRepository.insert(dietaAlimento)
            _isSaved.value = true // Para disparar la navegación hacia atrás
        }
    }

    companion object {
        private const val TAG = "SelectAlimentoVM"
    }
}
