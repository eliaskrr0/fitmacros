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
                "Alimento ${dietFood.foodId} añadido a dieta ${dietFood.dietId} en ${dietFood.mealType} (${dietFood.amount} ${dietFood.unit})"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al añadir alimento ${dietFood.foodId} a dieta ${dietFood.dietId} en ${dietFood.mealType}",
                ex
            )
            throw ex
        }
    }

    fun getAlimentosForDieta(dietId: Int): Flow<List<Meal>> {
        Log.d(TAG, "Cargando todos los alimentos para la dieta $dietId")
        return dietFoodDao.getAlimentosForDieta(dietId)
    }

    fun getAlimentosForDietaAndMeal(
        dietId: Int,
        mealType: MealType
    ): Flow<List<Meal>> {
        Log.d(TAG, "Cargando alimentos para dieta $dietId y comida $mealType")
        return dietFoodDao.getAlimentosForDietaAndMeal(dietId, mealType)
    }

    fun gettotalCaloriesForMeal(dietId: Int, mealType: MealType): Flow<Double?> {
        return dietFoodDao.gettotalCaloriesForMeal(dietId, mealType)
    }

    suspend fun delete(dietId: Int, foodId: Int, mealType: MealType) {
        try {
            dietFoodDao.delete(dietId, foodId, mealType)
            Log.i(
                TAG,
                "Alimento $foodId eliminado de la dieta $dietId en $mealType"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al eliminar alimento $foodId de la dieta $dietId en $mealType",
                ex
            )
            throw ex
        }
    }

    suspend fun updateCantidad(dietId: Int, foodId: Int, mealType: MealType, amount: Double) {
        try {
            dietFoodDao.updateCantidad(dietId, foodId, mealType, amount)
            Log.i(
                TAG,
                "Actualizada cantidad de alimento $foodId en dieta $dietId ($mealType) a $amount"
            )
        } catch (ex: Exception) {
            Log.e(
                TAG,
                "Error al actualizar cantidad de alimento $foodId en dieta $dietId ($mealType)",
                ex
            )
            throw ex
        }
    }

    companion object {
        private const val TAG = "DietaAlimentoRepo"
    }
}
