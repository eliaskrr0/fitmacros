package com.eliaskrr.fitmacros.data.repository.nutrition

import android.util.Log
import com.eliaskrr.fitmacros.data.dao.nutrition.DietDao
import com.eliaskrr.fitmacros.data.entity.nutrition.Diet
import kotlinx.coroutines.flow.Flow

class DietRepository(private val dietaDao: DietDao) {

    val allDietas: Flow<List<Diet>> = dietaDao.getAll()

    suspend fun insert(diet: Diet) {
        try {
            dietaDao.insert(diet)
            Log.i(TAG, "Dieta insertada: ${diet.nombre} (id=${diet.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al insertar dieta ${diet.nombre}", ex)
            throw ex
        }
    }

    suspend fun deleteDietas(ids: Set<Int>) {
        if (ids.isEmpty()) return

        try {
            dietaDao.deleteByIds(ids.toList())
            Log.i(TAG, "Dietas eliminadas: ${ids.joinToString()}")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al eliminar dietas ${ids.joinToString()}", ex)
            throw ex
        }
    }

    companion object {
        private const val TAG = "DietaRepository"
    }
}
