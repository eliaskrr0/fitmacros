package com.eliaskrr.fitmacros.data.repository.nutrition

import android.util.Log
import com.eliaskrr.fitmacros.data.dao.nutrition.FoodDao
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import kotlinx.coroutines.flow.Flow

class FoodRepository(private val foodDao: FoodDao) {

    fun getAllAlimentos(): Flow<List<Food>> = foodDao.getAll()

    fun getAlimentosByName(query: String): Flow<List<Food>> = foodDao.getByName(query)

    fun getById(id: Int): Flow<Food?> {
        Log.d(TAG, "Obteniendo alimento por id: $id")
        return foodDao.getById(id)
    }

    suspend fun insert(food: Food) {
        try {
            foodDao.insert(food)
            Log.i(TAG, "Alimento insertado: ${food.name} (id=${food.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al insertar alimento ${food.name}", ex)
            throw ex
        }
    }

    suspend fun update(food: Food) {
        try {
            foodDao.update(food)
            Log.i(TAG, "Alimento actualizado: ${food.name} (id=${food.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al actualizar alimento ${food.name} (id=${food.id})", ex)
            throw ex
        }
    }

    suspend fun delete(food: Food) {
        try {
            foodDao.delete(food)
            Log.i(TAG, "Alimento eliminado: ${food.name} (id=${food.id})")
        } catch (ex: Exception) {
            Log.e(TAG, "Error al eliminar alimento ${food.name} (id=${food.id})", ex)
            throw ex
        }
    }

    companion object {
        private const val TAG = "AlimentoRepository"
    }
}
