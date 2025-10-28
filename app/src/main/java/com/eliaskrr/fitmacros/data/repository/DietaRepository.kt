package com.eliaskrr.fitmacros.data.repository

import com.eliaskrr.fitmacros.data.dao.DietaDao
import com.eliaskrr.fitmacros.data.model.Dieta
import kotlinx.coroutines.flow.Flow

class DietaRepository(private val dietaDao: DietaDao) {

    val allDietas: Flow<List<Dieta>> = dietaDao.getAll()

    suspend fun insert(dieta: Dieta) {
        dietaDao.insert(dieta)
    }
}
