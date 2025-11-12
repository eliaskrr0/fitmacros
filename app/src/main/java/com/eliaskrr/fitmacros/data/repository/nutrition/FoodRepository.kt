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
        foodDao.insert(food)
        Log.i(TAG, "Alimento insertado: ${food.name} (id=${food.id})")
    }

    suspend fun update(food: Food) {
        val updatedFood = food.copy(updateDate = System.currentTimeMillis())
        foodDao.update(updatedFood)
        Log.i(TAG, "Alimento actualizado: ${updatedFood.name} (id=${updatedFood.id})")
    }

    suspend fun delete(food: Food) {
        foodDao.delete(food)
        Log.i(TAG, "Alimento eliminado: ${food.name} (id=${food.id})")
    }

    suspend fun deleteByIds(ids: Set<Int>) {
        if (ids.isEmpty()) return
        foodDao.deleteByIds(ids.toList())
        Log.i(TAG, "Alimentos eliminados: ${ids.joinToString()}")
    }

    companion object {
        private const val TAG = "AlimentoRepository"
    }
}
