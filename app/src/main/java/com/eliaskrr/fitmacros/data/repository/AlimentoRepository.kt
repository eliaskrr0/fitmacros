package com.eliaskrr.fitmacros.data.repository

import com.eliaskrr.fitmacros.data.dao.AlimentoDao
import com.eliaskrr.fitmacros.data.model.Alimento
import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val alimentoDao: AlimentoDao) {

    val allAlimentos: Flow<List<Alimento>> = alimentoDao.getAll()

    fun getById(id: Int): Flow<Alimento?> {
        return alimentoDao.getById(id)
    }

    suspend fun insert(alimento: Alimento) {
        alimentoDao.insert(alimento)
    }

    suspend fun update(alimento: Alimento) {
        alimentoDao.update(alimento)
    }

    suspend fun delete(alimento: Alimento) {
        alimentoDao.delete(alimento)
    }
}
