package com.eliaskrr.fitmacros.data.repository

import android.util.Log
import com.eliaskrr.fitmacros.data.dao.DietaDao
import com.eliaskrr.fitmacros.data.model.Dieta
import kotlinx.coroutines.flow.Flow

class DietaRepository(private val dietaDao: DietaDao) {

    val allDietas: Flow<List<Dieta>> = dietaDao.getAll()

    suspend fun insert(dieta: Dieta) {
        try {
            dietaDao.insert(dieta)
            Log.i(TAG, "Dieta insertada: ${dieta.nombre} (id=${dieta.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al insertar dieta ${dieta.nombre}", ex)
            throw ex
        }
    }

    companion object {
        private const val TAG = "DietaRepository"
    }
}
