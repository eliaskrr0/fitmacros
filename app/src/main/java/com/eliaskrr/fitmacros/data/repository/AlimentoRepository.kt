package com.eliaskrr.fitmacros.data.repository

import android.util.Log
import com.eliaskrr.fitmacros.data.dao.AlimentoDao
import com.eliaskrr.fitmacros.data.model.Alimento
import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val alimentoDao: AlimentoDao) {

    fun getAllAlimentos(): Flow<List<Alimento>> = alimentoDao.getAll()

    fun getAlimentosByName(query: String): Flow<List<Alimento>> = alimentoDao.getByName(query)

    fun getById(id: Int): Flow<Alimento?> {
        Log.d(TAG, "Obteniendo alimento por id: $id")
        return alimentoDao.getById(id)
    }

    suspend fun insert(alimento: Alimento) {
        try {
            alimentoDao.insert(alimento)
            Log.i(TAG, "Alimento insertado: ${alimento.nombre} (id=${alimento.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al insertar alimento ${alimento.nombre}", ex)
            throw ex
        }
    }

    suspend fun update(alimento: Alimento) {
        try {
            alimentoDao.update(alimento)
            Log.i(TAG, "Alimento actualizado: ${alimento.nombre} (id=${alimento.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al actualizar alimento ${alimento.nombre} (id=${alimento.id})", ex)
            throw ex
        }
    }

    suspend fun delete(alimento: Alimento) {
        try {
            alimentoDao.delete(alimento)
            Log.i(TAG, "Alimento eliminado: ${alimento.nombre} (id=${alimento.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al eliminar alimento ${alimento.nombre} (id=${alimento.id})", ex)
            throw ex
        }
    }

    companion object {
        private const val TAG = "AlimentoRepository"
    }
}
