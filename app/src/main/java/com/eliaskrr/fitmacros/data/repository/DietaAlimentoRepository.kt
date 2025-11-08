package com.eliaskrr.fitmacros.data.repository

import android.util.Log
import com.eliaskrr.fitmacros.data.dao.DietaAlimentoDao
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.DietaAlimentoWithAlimento
import com.eliaskrr.fitmacros.data.model.MealType
import kotlinx.coroutines.flow.Flow

class DietaAlimentoRepository(private val dietaAlimentoDao: DietaAlimentoDao) {

    suspend fun insert(dietaAlimento: DietaAlimento) {
        try {
            dietaAlimentoDao.insert(dietaAlimento)
            Log.i(
                TAG,
                "Alimento ${dietaAlimento.alimentoId} añadido a dieta ${dietaAlimento.dietaId} en ${dietaAlimento.mealType} (${dietaAlimento.cantidad} ${dietaAlimento.unidad})"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al añadir alimento ${dietaAlimento.alimentoId} a dieta ${dietaAlimento.dietaId} en ${dietaAlimento.mealType}",
                ex
            )
            throw ex
        }
    }

    fun getAlimentosForDieta(dietaId: Int): Flow<List<DietaAlimentoWithAlimento>> {
        Log.d(TAG, "Cargando todos los alimentos para la dieta $dietaId")
        return dietaAlimentoDao.getAlimentosForDieta(dietaId)
    }

    fun getAlimentosForDietaAndMeal(
        dietaId: Int,
        mealType: MealType
    ): Flow<List<DietaAlimentoWithAlimento>> {
        Log.d(TAG, "Cargando alimentos para dieta $dietaId y comida $mealType")
        return dietaAlimentoDao.getAlimentosForDietaAndMeal(dietaId, mealType)
    }

    fun getTotalCaloriasForMeal(dietaId: Int, mealType: MealType): Flow<Double?> {
        return dietaAlimentoDao.getTotalCaloriasForMeal(dietaId, mealType)
    }

    suspend fun delete(dietaId: Int, alimentoId: Int, mealType: MealType) {
        try {
            dietaAlimentoDao.delete(dietaId, alimentoId, mealType)
            Log.i(
                TAG,
                "Alimento $alimentoId eliminado de la dieta $dietaId en $mealType"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al eliminar alimento $alimentoId de la dieta $dietaId en $mealType",
                ex
            )
            throw ex
        }
    }

    suspend fun updateCantidad(dietaId: Int, alimentoId: Int, mealType: MealType, cantidad: Double) {
        try {
            dietaAlimentoDao.updateCantidad(dietaId, alimentoId, mealType, cantidad)
            Log.i(
                TAG,
                "Actualizada cantidad de alimento $alimentoId en dieta $dietaId ($mealType) a $cantidad"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al actualizar cantidad de alimento $alimentoId en dieta $dietaId ($mealType)",
                ex
            )
            throw ex
        }
    }

    companion object {
        private const val TAG = "DietaAlimentoRepo"
    }
}
