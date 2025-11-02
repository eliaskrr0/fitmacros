package com.eliaskrr.fitmacros

import android.app.Application
import com.eliaskrr.fitmacros.data.database.AppDatabase
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import com.eliaskrr.fitmacros.data.repository.DietaAlimentoRepository
import com.eliaskrr.fitmacros.data.repository.DietaRepository
import com.eliaskrr.fitmacros.data.repository.UserDataRepository

class FitMacrosApplication : Application() {
    // Usando by lazy para que la base de datos y el repositorio
    // solo se creen cuando se necesiten por primera vez.
    private val database by lazy { AppDatabase.getDatabase(this) }
    val alimentoRepository by lazy { AlimentoRepository(database.alimentoDao()) }
    val userDataRepository by lazy { UserDataRepository(this) }
    val dietaRepository by lazy { DietaRepository(database.dietaDao()) }
    val dietaAlimentoRepository by lazy { DietaAlimentoRepository(database.dietaAlimentoDao()) }
}
