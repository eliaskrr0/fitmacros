package com.eliaskrr.fitmacros.ui.alimento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlimentoViewModel(private val repository: AlimentoRepository) : ViewModel() {

    val allAlimentos: StateFlow<List<Alimento>> = repository.allAlimentos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(alimento: Alimento) = viewModelScope.launch {
        repository.insert(alimento)
    }

    fun update(alimento: Alimento) = viewModelScope.launch {
        repository.update(alimento)
    }

    fun delete(alimento: Alimento) = viewModelScope.launch {
        repository.delete(alimento)
    }
}

class AlimentoViewModelFactory(private val repository: AlimentoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlimentoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlimentoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
