package com.eliaskrr.fitmacros.data.repository.nutrition

import android.util.Log
import com.eliaskrr.fitmacros.data.dao.nutrition.DietFoodDao
import com.eliaskrr.fitmacros.data.entity.nutrition.DietFood
import com.eliaskrr.fitmacros.data.entity.nutrition.Meal
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import kotlinx.coroutines.flow.Flow

class DietFoodRepository(private val dietFoodDao: DietFoodDao) {

    suspend fun insert(dietFood: DietFood) {
        try {
            dietFoodDao.insert(dietFood)
            Log.i(
                TAG,
                "Alimento ${dietFood.alimentoId} añadido a dieta ${dietFood.dietaId} en ${dietFood.mealType} (${dietFood.cantidad} ${dietFood.unidad})"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al añadir alimento ${dietFood.alimentoId} a dieta ${dietFood.dietaId} en ${dietFood.mealType}",
                ex
            )
            throw ex
        }
    }

    fun getAlimentosForDieta(dietaId: Int): Flow<List<Meal>> {
        Log.d(TAG, "Cargando todos los alimentos para la dieta $dietaId")
        return dietFoodDao.getAlimentosForDieta(dietaId)
    }

    fun getAlimentosForDietaAndMeal(
        dietaId: Int,
        mealType: MealType
    ): Flow<List<Meal>> {
        Log.d(TAG, "Cargando alimentos para dieta $dietaId y comida $mealType")
        return dietFoodDao.getAlimentosForDietaAndMeal(dietaId, mealType)
    }

    fun getTotalCaloriasForMeal(dietaId: Int, mealType: MealType): Flow<Double?> {
        return dietFoodDao.getTotalCaloriasForMeal(dietaId, mealType)
    }

    suspend fun delete(dietaId: Int, alimentoId: Int, mealType: MealType) {
        try {
            dietFoodDao.delete(dietaId, alimentoId, mealType)
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
            dietFoodDao.updateCantidad(dietaId, alimentoId, mealType, cantidad)
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
