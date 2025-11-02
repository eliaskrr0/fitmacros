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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AddAlimentoToDietaViewModel(
    savedStateHandle: SavedStateHandle,
    private val alimentoRepository: AlimentoRepository,
    private val dietaAlimentoRepository: DietaAlimentoRepository
) : ViewModel() {

    private val dietaId: Int = checkNotNull(savedStateHandle["dietaId"])
    private val mealTypeName: String = checkNotNull(savedStateHandle["mealType"])
    val mealType: MealType = MealType.valueOf(mealTypeName)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val alimentos: StateFlow<List<Alimento>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                alimentoRepository.getAllAlimentos()
            } else {
                alimentoRepository.getAlimentosByName(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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
            _uiEvent.emit(UiEvent.AlimentoAdded)
        }
    }

    sealed class UiEvent {
        data object AlimentoAdded : UiEvent()
    }

    companion object {
        fun provideFactory(
            alimentoRepository: AlimentoRepository,
            dietaAlimentoRepository: DietaAlimentoRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return AddAlimentoToDietaViewModel(
                    savedStateHandle = extras.createSavedStateHandle(),
                    alimentoRepository = alimentoRepository,
                    dietaAlimentoRepository = dietaAlimentoRepository
                ) as T
            }
        }
    }
}
