package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eliaskrr.fitmacros.data.model.Food
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query("SELECT * FROM tb_alimentos ORDER BY nombre ASC")
    fun getAll(): Flow<List<Food>>

    @Query("SELECT * FROM tb_alimentos WHERE id = :id")
    fun getById(id: Int): Flow<Food?>

    @Query("SELECT * FROM tb_alimentos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun getByName(query: String): Flow<List<Food>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food)

    @Update
    suspend fun update(food: Food)

    @Delete
    suspend fun delete(food: Food)
}
