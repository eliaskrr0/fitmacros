package com.eliaskrr.fitmacros

import android.app.Application
import com.eliaskrr.fitmacros.data.database.AppDatabase
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository

class FitMacrosApplication : Application() {
    // Usando by lazy para que la base de datos y el repositorio
    // solo se creen cuando se necesiten por primera vez.
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AlimentoRepository(database.alimentoDao()) }
}
