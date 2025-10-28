package com.eliaskrr.fitmacros.ui.dieta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.Dieta
import com.eliaskrr.fitmacros.data.repository.DietaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DietaViewModel(private val repository: DietaRepository) : ViewModel() {

    val allDietas: StateFlow<List<Dieta>> = repository.allDietas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(dieta: Dieta) = viewModelScope.launch {
        repository.insert(dieta)
    }
}

class DietaViewModelFactory(private val repository: DietaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
