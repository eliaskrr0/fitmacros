package com.eliaskrr.fitmacros.data.repository

import com.eliaskrr.fitmacros.data.dao.DietaAlimentoDao
import com.eliaskrr.fitmacros.data.model.AlimentoConCantidad
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.MealType
import kotlinx.coroutines.flow.Flow

class DietaAlimentoRepository(private val dietaAlimentoDao: DietaAlimentoDao) {

    suspend fun insert(dietaAlimento: DietaAlimento) {
        dietaAlimentoDao.insert(dietaAlimento)
    }

    fun getAlimentosForDietaAndMeal(dietaId: Int, mealType: MealType): Flow<List<AlimentoConCantidad>> {
        return dietaAlimentoDao.getAlimentosForDietaAndMeal(dietaId, mealType)
    }

    fun getTotalCaloriasForMeal(dietaId: Int, mealType: MealType): Flow<Double?> {
        return dietaAlimentoDao.getTotalCaloriasForMeal(dietaId, mealType)
    }
}
