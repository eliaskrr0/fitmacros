package com.eliaskrr.fitmacros.ui.alimento

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
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
class AlimentoViewModel @Inject constructor(
    private val repository: AlimentoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val alimentos: StateFlow<List<Alimento>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                Log.d(TAG, "Buscando todos los alimentos")
                repository.getAllAlimentos()
            } else {
                Log.d(TAG, "Buscando alimentos por nombre: $query")
                repository.getAlimentosByName(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun insert(alimento: Alimento) = viewModelScope.launch {
        runCatching {
            Log.d(TAG, "Solicitando inserción de alimento ${alimento.nombre}")
            repository.insert(alimento)
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando inserción de ${alimento.nombre}", ex)
        }
    }

    fun update(alimento: Alimento) = viewModelScope.launch {
        runCatching {
            Log.d(TAG, "Solicitando actualización de alimento ${alimento.nombre} (id=${alimento.id})")
            repository.update(alimento)
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando actualización de ${alimento.nombre} (id=${alimento.id})", ex)
        }
    }

    fun delete(alimento: Alimento) = viewModelScope.launch {
        runCatching {
            Log.d(TAG, "Solicitando eliminación de alimento ${alimento.nombre} (id=${alimento.id})")
            repository.delete(alimento)
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando eliminación de ${alimento.nombre} (id=${alimento.id})", ex)
        }
    }

    companion object {
        private const val TAG = "AlimentoViewModel"
    }
}
