package com.eliaskrr.fitmacros.ui.dieta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.model.Dieta
import com.eliaskrr.fitmacros.data.repository.DietaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietaViewModel @Inject constructor(
    private val repository: DietaRepository
) : ViewModel() {

    val allDietas: StateFlow<List<Dieta>> = repository.allDietas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(dieta: Dieta) = viewModelScope.launch {
        runCatching {
            Log.d(TAG, "Solicitando inserción de dieta ${dieta.nombre}")
            repository.insert(dieta)
        }.onFailure { ex ->
            Log.e(TAG, "Error solicitando inserción de dieta ${dieta.nombre}", ex)
        }
    }

    companion object {
        private const val TAG = "DietaViewModel"
    }
}
