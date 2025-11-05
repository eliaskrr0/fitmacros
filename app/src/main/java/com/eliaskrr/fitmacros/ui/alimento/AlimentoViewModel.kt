package com.eliaskrr.fitmacros.ui.alimento

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.R
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlimentoViewModel @Inject constructor(
    private val repository: AlimentoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(AlimentosUiState(isLoading = true))
    val uiState: StateFlow<AlimentosUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AlimentoEvent>()
    val events: SharedFlow<AlimentoEvent> = _events.asSharedFlow()

    init {
        observeAlimentos()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun insert(alimento: Alimento) = viewModelScope.launch {
        try {
            Log.d(TAG, "Solicitando inserción de alimento ${alimento.nombre}")
            repository.insert(alimento)
        } catch (ex: Exception) {
            Log.e(TAG, "Error solicitando inserción de ${alimento.nombre}", ex)
            notifyError(R.string.error_saving_alimento)
        }
    }

    fun update(alimento: Alimento) = viewModelScope.launch {
        try {
            Log.d(TAG, "Solicitando actualización de alimento ${alimento.nombre} (id=${alimento.id})")
            repository.update(alimento)
        } catch (ex: Exception) {
            Log.e(TAG, "Error solicitando actualización de ${alimento.nombre} (id=${alimento.id})", ex)
            notifyError(R.string.error_saving_alimento)
        }
    }

    fun delete(alimento: Alimento) = viewModelScope.launch {
        try {
            Log.d(TAG, "Solicitando eliminación de alimento ${alimento.nombre} (id=${alimento.id})")
            repository.delete(alimento)
        } catch (ex: Exception) {
            Log.e(TAG, "Error solicitando eliminación de ${alimento.nombre} (id=${alimento.id})", ex)
            notifyError(R.string.error_deleting_alimento)
        }
    }

    private fun observeAlimentos() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val alimentosFlow = if (query.isBlank()) {
                    Log.d(TAG, "Buscando todos los alimentos")
                    repository.getAllAlimentos()
                } else {
                    Log.d(TAG, "Buscando alimentos por nombre: $query")
                    repository.getAlimentosByName(query)
                }

                alimentosFlow
                    .catch { ex ->
                        Log.e(TAG, "Error cargando alimentos para la búsqueda '$query'", ex)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                alimentos = emptyList(),
                                errorMessage = R.string.error_loading_alimentos
                            )
                        }
                        _events.emit(AlimentoEvent.ShowMessage(R.string.error_loading_alimentos))
                    }
                    .collect { alimentos ->
                        _uiState.update {
                            it.copy(
                                alimentos = alimentos,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
            }
        }
    }

    private suspend fun notifyError(messageRes: Int) {
        _uiState.update { it.copy(errorMessage = messageRes) }
        _events.emit(AlimentoEvent.ShowMessage(messageRes))
    }

    companion object {
        private const val TAG = "AlimentoViewModel"
    }

    data class AlimentosUiState(
        val alimentos: List<Alimento> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: Int? = null
    )

    sealed interface AlimentoEvent {
        data class ShowMessage(val messageRes: Int) : AlimentoEvent
    }
}
