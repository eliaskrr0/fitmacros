package com.eliaskrr.fitmacros.data.dao.nutrition

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eliaskrr.fitmacros.data.entity.nutrition.Diet
import kotlinx.coroutines.flow.Flow

@Dao
interface DietDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diet: Diet)

    @Query("SELECT * FROM tb_diet ORDER BY nombre ASC")
    fun getAll(): Flow<List<Diet>>

    @Query("DELETE FROM tb_diet WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}
