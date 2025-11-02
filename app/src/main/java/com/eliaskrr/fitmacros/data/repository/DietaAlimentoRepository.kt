package com.eliaskrr.fitmacros.data.repository

import com.eliaskrr.fitmacros.data.dao.DietaAlimentoDao
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.DietaAlimentoWithAlimento
import kotlinx.coroutines.flow.Flow

class DietaAlimentoRepository(private val dietaAlimentoDao: DietaAlimentoDao) {

    fun getByDieta(dietaId: Int): Flow<List<DietaAlimentoWithAlimento>> =
        dietaAlimentoDao.getByDieta(dietaId)

    suspend fun insert(dietaAlimento: DietaAlimento) {
        dietaAlimentoDao.insert(dietaAlimento)
    }

    suspend fun deleteById(id: Int) {
        dietaAlimentoDao.deleteById(id)
    }
}
