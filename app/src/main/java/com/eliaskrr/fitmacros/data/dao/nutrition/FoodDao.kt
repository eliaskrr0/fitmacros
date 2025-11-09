package com.eliaskrr.fitmacros.data.dao.nutrition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query("SELECT * FROM tb_food ORDER BY name ASC")
    fun getAll(): Flow<List<Food>>

    @Query("SELECT * FROM tb_food WHERE id = :id")
    fun getById(id: Int): Flow<Food?>

    @Query("SELECT * FROM tb_food WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun getByName(query: String): Flow<List<Food>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food)

    @Update
    suspend fun update(food: Food)

    @Delete
    suspend fun delete(food: Food)
}
