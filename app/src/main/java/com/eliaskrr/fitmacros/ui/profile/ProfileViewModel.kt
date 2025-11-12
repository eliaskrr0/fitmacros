package com.eliaskrr.fitmacros.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliaskrr.fitmacros.data.repository.user.UserData
import com.eliaskrr.fitmacros.data.repository.user.UserDataRepository
import com.eliaskrr.fitmacros.domain.MacroCalculator
import com.eliaskrr.fitmacros.domain.MacroCalculationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val userData: StateFlow<UserData> = userDataRepository.userData
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserData("", "", "", "", "", "", "")
        )

    val calculationResult: StateFlow<MacroCalculationResult> = userData
        .map {
            MacroCalculator.calculate(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MacroCalculationResult.Idle
    )

    fun saveUserData(userData: UserData) {
        viewModelScope.launch {
            runCatching {
                Log.d(TAG, "Guardando datos de usuario para ${userData.name}")
                userDataRepository.saveUserData(userData)
            }.onSuccess {
                Log.i(TAG, "Datos de usuario guardados para ${userData.name}")
            }.onFailure { ex ->
                Log.e(TAG, "Error al guardar datos de usuario para ${userData.name}", ex)
            }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
